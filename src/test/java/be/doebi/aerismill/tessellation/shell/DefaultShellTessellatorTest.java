package be.doebi.aerismill.tessellation.shell;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;
import be.doebi.aerismill.tessellation.face.FaceMeshPatch;
import be.doebi.aerismill.tessellation.face.FaceTessellator;
import be.doebi.aerismill.tessellation.face.SurfaceFamily;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultShellTessellatorTest {

    @Test
    void tessellate_emptyShell_returnsEmptyMesh() {
        DefaultShellTessellator tessellator = new DefaultShellTessellator(face -> {
            fail("Face tessellator should not be called for empty shell.");
            return null;
        });

        ShellGeom shell = new ShellGeom("#shell1", List.of());

        Mesh result = tessellator.tessellate(shell);

        assertNotNull(result);
        assertNotNull(result.vertices());
        assertNotNull(result.triangles());
        assertTrue(result.vertices().isEmpty());
        assertTrue(result.triangles().isEmpty());
    }

    @Test
    void tessellate_singleFace_returnsSinglePatchAsMesh() {
        FaceGeom face = face("#face1");

        FaceMeshPatch patch = new FaceMeshPatch(
                List.of(
                        p(0, 0, 0),
                        p(1, 0, 0),
                        p(0, 1, 0)
                ),
                List.of(new int[]{0, 1, 2}),
                SurfaceFamily.PLANAR
        );

        RecordingFaceTessellator faceTessellator = new RecordingFaceTessellator(List.of(patch));
        DefaultShellTessellator tessellator = new DefaultShellTessellator(faceTessellator);

        ShellGeom shell = new ShellGeom("#shell1", List.of(face));

        Mesh result = tessellator.tessellate(shell);

        assertEquals(List.of(face), faceTessellator.seenFaces());

        assertEquals(3, result.vertices().size());
        assertEquals(new MeshVertex(0, p(0, 0, 0)), result.vertices().get(0));
        assertEquals(new MeshVertex(1, p(1, 0, 0)), result.vertices().get(1));
        assertEquals(new MeshVertex(2, p(0, 1, 0)), result.vertices().get(2));

        assertEquals(1, result.triangles().size());
        assertEquals(new MeshTriangle(0, 1, 2), result.triangles().get(0));
    }

    @Test
    void tessellate_multipleFaces_appendsVerticesAndOffsetsTriangleIndices() {
        FaceGeom face1 = face("#face1");
        FaceGeom face2 = face("#face2");

        FaceMeshPatch patch1 = new FaceMeshPatch(
                List.of(
                        p(0, 0, 0),
                        p(1, 0, 0),
                        p(0, 1, 0)
                ),
                List.of(new int[]{0, 1, 2}),
                SurfaceFamily.PLANAR
        );

        FaceMeshPatch patch2 = new FaceMeshPatch(
                List.of(
                        p(10, 0, 0),
                        p(11, 0, 0),
                        p(10, 1, 0)
                ),
                List.of(new int[]{0, 2, 1}),
                SurfaceFamily.PLANAR
        );

        RecordingFaceTessellator faceTessellator = new RecordingFaceTessellator(List.of(patch1, patch2));
        DefaultShellTessellator tessellator = new DefaultShellTessellator(faceTessellator);

        ShellGeom shell = new ShellGeom("#shell1", List.of(face1, face2));

        Mesh result = tessellator.tessellate(shell);

        assertEquals(List.of(face1, face2), faceTessellator.seenFaces());

        assertEquals(6, result.vertices().size());
        assertEquals(new MeshVertex(0, p(0, 0, 0)), result.vertices().get(0));
        assertEquals(new MeshVertex(1, p(1, 0, 0)), result.vertices().get(1));
        assertEquals(new MeshVertex(2, p(0, 1, 0)), result.vertices().get(2));
        assertEquals(new MeshVertex(3, p(10, 0, 0)), result.vertices().get(3));
        assertEquals(new MeshVertex(4, p(11, 0, 0)), result.vertices().get(4));
        assertEquals(new MeshVertex(5, p(10, 1, 0)), result.vertices().get(5));

        assertEquals(2, result.triangles().size());
        assertEquals(new MeshTriangle(0, 1, 2), result.triangles().get(0));
        assertEquals(new MeshTriangle(3, 5, 4), result.triangles().get(1));
    }

    @Test
    void tessellate_propagatesFaceTessellationFailure() {
        FaceTessellator faceTessellator = face -> {
            throw new IllegalArgumentException("Only planar faces are supported for now.");
        };

        DefaultShellTessellator tessellator = new DefaultShellTessellator(faceTessellator);
        ShellGeom shell = new ShellGeom("#shell1", List.of(face("#face1")));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(shell)
        );

        assertEquals("Only planar faces are supported for now.", ex.getMessage());
    }

    private static FaceGeom face(String stepId) {
        return new FaceGeom(stepId, null, List.of(), true);
    }

    private static Point3 p(double x, double y, double z) {
        return new Point3(x, y, z);
    }

    private static class RecordingFaceTessellator implements FaceTessellator {
        private final List<FaceMeshPatch> patches;
        private final List<FaceGeom> seenFaces = new ArrayList<>();
        private int index = 0;

        private RecordingFaceTessellator(List<FaceMeshPatch> patches) {
            this.patches = patches;
        }

        @Override
        public FaceMeshPatch tessellate(FaceGeom face) {
            seenFaces.add(face);
            return patches.get(index++);
        }

        List<FaceGeom> seenFaces() {
            return seenFaces;
        }
    }
}