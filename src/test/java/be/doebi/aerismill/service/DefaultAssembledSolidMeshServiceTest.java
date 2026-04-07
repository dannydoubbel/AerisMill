package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;
import be.doebi.aerismill.tessellation.solid.SolidTessellator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultAssembledSolidMeshServiceTest {

    @Test
    void constructor_rejectsNullSolidTessellator() {
        assertThrows(
                NullPointerException.class,
                () -> new DefaultAssembledSolidMeshService(null)
        );
    }

    @Test
    void generateMesh_solidGeom_delegatesToSolidTessellator() {
        StubSolidTessellator tessellator = new StubSolidTessellator(mesh());
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        SolidGeom solid = solidGeom("#solid1");
        AssembledSolidResult assembled = new AssembledSolidResult("#1", solid);

        service.generateMesh(assembled);

        assertEquals(1, tessellator.callCount());
        assertSame(solid, tessellator.lastSolid());
    }

    @Test
    void generateMesh_solidGeom_returnsDelegatedMesh() {
        Mesh expected = mesh();
        StubSolidTessellator tessellator = new StubSolidTessellator(expected);
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        SolidGeom solid = solidGeom("#solid1");
        AssembledSolidResult assembled = new AssembledSolidResult("#1", solid);

        Mesh result = service.generateMesh(assembled);

        assertSame(expected, result);
    }



    @Test
    void generateMesh_nullInput_throwsNullPointerException() {
        StubSolidTessellator tessellator = new StubSolidTessellator(mesh());
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> service.generateMesh(null)
        );

        assertEquals("assembledSolidResult must not be null", ex.getMessage());
    }





    @Test
    void generateMesh_solidWithVoidsGeom_offsetsTriangleIndicesCorrectly() {
        RecordingSolidTessellator tessellator = new RecordingSolidTessellator();
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        SolidWithVoidsGeom solidWithVoids = solidWithVoidsGeom("#solid2");
        AssembledSolidResult assembled = new AssembledSolidResult("#2", solidWithVoids);

        tessellator.willReturn("#solid2:outer", singleTriangleMesh());
        tessellator.willReturn("#solid2:void[0]", singleTriangleMesh());

        Mesh result = service.generateMesh(assembled);

        assertEquals(2, result.triangleCount());

        MeshTriangle first = result.triangles().get(0);
        MeshTriangle second = result.triangles().get(1);

        assertEquals(0, first.a());
        assertEquals(1, first.b());
        assertEquals(2, first.c());

        assertEquals(3, second.a());
        assertEquals(4, second.b());
        assertEquals(5, second.c());
    }

    @Test
    void generateMesh_solidWithVoidsGeom_combinesOuterAndVoidShellMeshes() {
        RecordingSolidTessellator tessellator = new RecordingSolidTessellator();
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        SolidWithVoidsGeom solidWithVoids = solidWithVoidsGeom("#solid2");
        AssembledSolidResult assembled = new AssembledSolidResult("#2", solidWithVoids);

        Mesh outerMesh = singleTriangleMesh();
        Mesh voidMesh = singleTriangleMesh();

        tessellator.willReturn("#solid2:outer", outerMesh);
        tessellator.willReturn("#solid2:void[0]", voidMesh);

        Mesh result = service.generateMesh(assembled);

        assertEquals(6, result.vertexCount());
        assertEquals(2, result.triangleCount());

        assertEquals(2, tessellator.callCount());
        assertEquals(List.of("#solid2:outer", "#solid2:void[0]"), tessellator.stepIds());
    }

    @Test
    void generateMesh_solidWithVoidsGeom_throwsWhenOuterShellFails() {
        RecordingSolidTessellator tessellator = new RecordingSolidTessellator();
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        SolidWithVoidsGeom solidWithVoids = solidWithVoidsGeom("#solid2");
        AssembledSolidResult assembled = new AssembledSolidResult("#2", solidWithVoids);

        tessellator.willThrow(
                "#solid2:outer",
                new UnsupportedOperationException("outer shell failed")
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.generateMesh(assembled)
        );

        assertTrue(ex.getMessage().contains("Outer shell of solid #solid2 is not previewable"));
        assertTrue(ex.getMessage().contains("outer shell failed"));

        assertEquals(1, tessellator.callCount());
        assertEquals(List.of("#solid2:outer"), tessellator.stepIds());
    }

    @Test
    void generateMesh_solidWithVoidsGeom_throwsWhenAnyVoidShellFails() {
        RecordingSolidTessellator tessellator = new RecordingSolidTessellator();
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        SolidWithVoidsGeom solidWithVoids = solidWithVoidsGeom("#solid2");
        AssembledSolidResult assembled = new AssembledSolidResult("#2", solidWithVoids);

        tessellator.willReturn("#solid2:outer", singleTriangleMesh());
        tessellator.willThrow(
                "#solid2:void[0]",
                new UnsupportedOperationException("void shell failed")
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.generateMesh(assembled)
        );

        assertTrue(ex.getMessage().contains("Void shell 0 of solid #solid2 is not previewable"));
        assertTrue(ex.getMessage().contains("void shell failed"));

        assertEquals(2, tessellator.callCount());
        assertEquals(
                List.of("#solid2:outer", "#solid2:void[0]"),
                tessellator.stepIds()
        );
    }

    @Test
    void generateMesh_solidWithVoidsGeom_combinesMultipleVoidShellMeshes() {
        RecordingSolidTessellator tessellator = new RecordingSolidTessellator();
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        SolidWithVoidsGeom solidWithVoids = new SolidWithVoidsGeom(
                "#solid2",
                shell("#solid2:outerShell"),
                List.of(
                        shell("#solid2:voidShell0"),
                        shell("#solid2:voidShell1")
                )
        );

        AssembledSolidResult assembled = new AssembledSolidResult("#2", solidWithVoids);

        tessellator.willReturn("#solid2:outer", singleTriangleMesh());
        tessellator.willReturn("#solid2:void[0]", singleTriangleMesh());
        tessellator.willReturn("#solid2:void[1]", singleTriangleMesh());

        Mesh result = service.generateMesh(assembled);

        assertEquals(9, result.vertexCount());
        assertEquals(3, result.triangleCount());

        assertEquals(3, tessellator.callCount());
        assertEquals(
                List.of("#solid2:outer", "#solid2:void[0]", "#solid2:void[1]"),
                tessellator.stepIds()
        );
    }


    private Mesh singleTriangleMesh() {
        return new Mesh(
                List.of(
                        new MeshVertex(0, new Point3(0.0, 0.0, 0.0)),
                        new MeshVertex(1, new Point3(1.0, 0.0, 0.0)),
                        new MeshVertex(2, new Point3(0.0, 1.0, 0.0))
                ),
                List.of(
                        new MeshTriangle(0, 1, 2)
                )
        );
    }




    private SolidGeom solidGeom(String stepId) {
        return new SolidGeom(stepId, shell(stepId + "_outer"));
    }


    private SolidWithVoidsGeom solidWithVoidsGeom(String stepId) {
        return new SolidWithVoidsGeom(
                stepId,
                shell(stepId + "_outer"),
                List.of(shell(stepId + "_void1"))
        );
    }







    private ShellGeom shell(String stepId) {
        return new ShellGeom(stepId, List.of());
    }

    private Mesh mesh() {
        return new Mesh(List.of(), List.of());
    }

    private static final class StubSolidTessellator implements SolidTessellator {
        private final Mesh meshToReturn;
        private int callCount;
        private SolidGeom lastSolid;

        private StubSolidTessellator(Mesh meshToReturn) {
            this.meshToReturn = meshToReturn;
        }

        @Override
        public Mesh tessellate(SolidGeom solid) {
            callCount++;
            lastSolid = solid;
            return meshToReturn;
        }

        private int callCount() {
            return callCount;
        }

        private SolidGeom lastSolid() {
            return lastSolid;
        }
    }




    private static final class RecordingSolidTessellator implements SolidTessellator {
        private final java.util.Map<String, Mesh> meshesByStepId = new java.util.HashMap<>();
        private final java.util.Map<String, RuntimeException> failuresByStepId = new java.util.HashMap<>();
        private final java.util.List<String> stepIds = new java.util.ArrayList<>();

        void willReturn(String stepId, Mesh mesh) {
            meshesByStepId.put(stepId, mesh);
        }

        void willThrow(String stepId, RuntimeException ex) {
            failuresByStepId.put(stepId, ex);
        }

        int callCount() {
            return stepIds.size();
        }

        java.util.List<String> stepIds() {
            return stepIds;
        }

        @Override
        public Mesh tessellate(SolidGeom solid) {
            stepIds.add(solid.stepId());

            RuntimeException failure = failuresByStepId.get(solid.stepId());
            if (failure != null) {
                throw failure;
            }

            Mesh mesh = meshesByStepId.get(solid.stepId());
            if (mesh == null) {
                throw new AssertionError("No stubbed mesh for stepId: " + solid.stepId());
            }

            return mesh;
        }
    }












}