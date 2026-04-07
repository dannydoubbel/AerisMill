package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;

import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;
import be.doebi.aerismill.tessellation.solid.SolidTessellator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultAssembledSolidMeshService implements AssembledSolidMeshService {

    private final SolidTessellator solidTessellator;

    public DefaultAssembledSolidMeshService(SolidTessellator solidTessellator) {
        this.solidTessellator = Objects.requireNonNull(solidTessellator);
    }

    @Override
    public Mesh generateMesh(AssembledSolidResult assembledSolidResult) {
        Objects.requireNonNull(assembledSolidResult, "assembledSolidResult must not be null");

        Object geom = extractGeomPayload(assembledSolidResult);

        if (geom instanceof SolidGeom solid) {
            return solidTessellator.tessellate(solid);
        }

        if (geom instanceof SolidWithVoidsGeom solidWithVoids) {
            return generateMeshForSolidWithVoids(solidWithVoids);
        }

        throw new IllegalArgumentException("Unsupported assembled solid payload: " + geom);
    }

    private Mesh generateMeshForSolidWithVoids(SolidWithVoidsGeom solidWithVoids) {
        String solidStepId = solidWithVoids.stepId();

        Mesh combinedMesh;
        try {
            combinedMesh = tessellateShellAsTemporarySolid(
                    solidStepId + ":outer",
                    solidWithVoids.outerShell()
            );
        } catch (IllegalArgumentException | UnsupportedOperationException ex) {
            throw new IllegalArgumentException(
                    "Outer shell of solid " + solidStepId + " is not previewable: " + ex.getMessage(),
                    ex
            );
        }

        List<ShellGeom> voidShells = solidWithVoids.voidShells();
        if (voidShells == null) {
            throw new IllegalArgumentException("SolidWithVoidsGeom void shells must not be null");
        }

        for (int i = 0; i < voidShells.size(); i++) {
            ShellGeom voidShell = voidShells.get(i);

            Mesh voidMesh;
            try {
                voidMesh = tessellateShellAsTemporarySolid(
                        solidStepId + ":void[" + i + "]",
                        voidShell
                );
            } catch (IllegalArgumentException | UnsupportedOperationException ex) {
                throw new IllegalArgumentException(
                        "Void shell " + i + " of solid " + solidStepId + " is not previewable: " + ex.getMessage(),
                        ex
                );
            }

            combinedMesh = appendMesh(combinedMesh, voidMesh);
        }

        return combinedMesh;
    }

    private Mesh tessellateShellAsTemporarySolid(String temporaryStepId, ShellGeom shell) {
        if (shell == null) {
            throw new IllegalArgumentException("Shell must not be null");
        }

        SolidGeom temporarySolid = new SolidGeom(temporaryStepId, shell);
        return solidTessellator.tessellate(temporarySolid);
    }

    private Mesh appendMesh(Mesh base, Mesh addition) {
        Objects.requireNonNull(base, "base mesh must not be null");
        Objects.requireNonNull(addition, "addition mesh must not be null");

        List<MeshVertex> vertices = new ArrayList<>(
                base.vertices().size() + addition.vertices().size()
        );
        vertices.addAll(base.vertices());

        int vertexOffset = base.vertices().size();
        vertices.addAll(addition.vertices());

        List<MeshTriangle> triangles = new ArrayList<>(
                base.triangles().size() + addition.triangles().size()
        );
        triangles.addAll(base.triangles());

        for (MeshTriangle triangle : addition.triangles()) {
            triangles.add(new MeshTriangle(
                    triangle.a() + vertexOffset,
                    triangle.b() + vertexOffset,
                    triangle.c() + vertexOffset
            ));
        }

        return new Mesh(vertices, triangles);
    }

    private Object extractGeomPayload(AssembledSolidResult assembledSolidResult) {
        return assembledSolidResult.solid();
    }
}