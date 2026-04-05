package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.tessellation.solid.SolidTessellator;

import java.util.Objects;

public class DefaultAssembledSolidMeshService implements AssembledSolidMeshService {

    private final SolidTessellator solidTessellator;

    public DefaultAssembledSolidMeshService(SolidTessellator solidTessellator) {
        this.solidTessellator = Objects.requireNonNull(solidTessellator);
    }

    @Override
    public     Mesh generateMesh(AssembledSolidResult assembledSolidResult) {
        Objects.requireNonNull(assembledSolidResult, "assembledSolidResult must not be null");

        Object geom = extractGeomPayload(assembledSolidResult);

        if (geom instanceof SolidGeom solid) {
            return solidTessellator.tessellate(solid);
        }

        if (geom instanceof SolidWithVoidsGeom) {
            throw new UnsupportedOperationException("SolidWithVoidsGeom is not supported yet");
        }

        throw new IllegalArgumentException("Unsupported assembled solid payload: " + geom);
    }

    private Object extractGeomPayload(Object assembledSolidResult) {
        // adapt this to your actual assembled result type
        // e.g. return ((AssembledSolidResult) assembledSolidResult).solid();
        throw new UnsupportedOperationException("Implement extraction against actual assembled result type");
    }
}