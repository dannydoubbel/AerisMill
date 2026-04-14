package be.doebi.aerismill.tessellation.solid;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;
import be.doebi.aerismill.tessellation.shell.DebugSurfaceFamilyMeshes;
import be.doebi.aerismill.tessellation.shell.ShellTessellator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultSolidTessellatorTest {

    @Test
    void constructor_throws_whenShellTessellatorIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new DefaultSolidTessellator(null)
        );

        assertEquals("Shell tessellator must not be null.", ex.getMessage());
    }

    @Test
    void tessellate_throws_whenSolidIsNull() {
        DefaultSolidTessellator tessellator = new DefaultSolidTessellator(stubShellTessellatorReturning(emptyMesh()));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(null)
        );

        assertEquals("Solid must not be null.", ex.getMessage());
    }

    @Test
    void tessellate_throws_whenOuterShellIsNull() {
        DefaultSolidTessellator tessellator = new DefaultSolidTessellator(stubShellTessellatorReturning(emptyMesh()));
        SolidGeom solid = new SolidGeom("#solid1", null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(solid)
        );

        assertEquals("Solid outer shell must not be null.", ex.getMessage());
    }

    @Test
    void tessellate_delegatesToShellTessellator() {
        ShellGeom shell = new ShellGeom("#shell1", List.of(face("#face1")));
        SolidGeom solid = new SolidGeom("#solid1", shell);

        RecordingShellTessellator shellTessellator = new RecordingShellTessellator(emptyMesh());
        DefaultSolidTessellator tessellator = new DefaultSolidTessellator(shellTessellator);

        tessellator.tessellate(solid);

        assertSame(shell, shellTessellator.seenShell());
    }

    @Test
    void tessellate_returnsShellMeshUnchanged() {
        ShellGeom shell = new ShellGeom("#shell1", List.of(face("#face1")));
        SolidGeom solid = new SolidGeom("#solid1", shell);

        Mesh mesh = new Mesh(
                List.of(
                        new MeshVertex(0, new Point3(0, 0, 0)),
                        new MeshVertex(1, new Point3(1, 0, 0)),
                        new MeshVertex(2, new Point3(0, 1, 0))
                ),
                List.of(
                        new MeshTriangle(0, 1, 2)
                )
        );

        DefaultSolidTessellator tessellator = new DefaultSolidTessellator(stubShellTessellatorReturning(emptyMesh()));

        Mesh result = tessellator.tessellate(solid);

        assertSame(mesh, result);
    }

    private static Mesh emptyMesh() {
        return new Mesh(List.of(), List.of());
    }

    private static FaceGeom face(String stepId) {
        return new FaceGeom(stepId, null, List.of(), true);
    }

    private static class RecordingShellTessellator implements ShellTessellator {
        private final Mesh meshToReturn;
        private ShellGeom seenShell;

        private RecordingShellTessellator(Mesh meshToReturn) {
            this.meshToReturn = meshToReturn;
        }

        @Override
        public Mesh tessellate(ShellGeom shell) {
            this.seenShell = shell;
            return meshToReturn;
        }

        @Override
        public DebugSurfaceFamilyMeshes tessellateDebugSurfaceFamilies(ShellGeom shell) {
            throw new UnsupportedOperationException(
                    "Debug surface-family tessellation is only supported by PreviewShellTessellator."
            );
        }

        ShellGeom seenShell() {
            return seenShell;
        }


    }

    private ShellTessellator stubShellTessellatorReturning(Mesh mesh) {
        return new ShellTessellator() {
            @Override
            public Mesh tessellate(ShellGeom shell) {
                return mesh;
            }

            @Override
            public DebugSurfaceFamilyMeshes tessellateDebugSurfaceFamilies(ShellGeom shell) {
                throw new UnsupportedOperationException("Not needed in this test.");
            }
        };
    }
}