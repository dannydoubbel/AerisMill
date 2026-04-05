package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.mesh.Mesh;
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
    void generateMesh_solidWithVoidsGeom_throwsUnsupportedOperationException() {
        StubSolidTessellator tessellator = new StubSolidTessellator(mesh());
        DefaultAssembledSolidMeshService service = new DefaultAssembledSolidMeshService(tessellator);

        SolidWithVoidsGeom solidWithVoids = solidWithVoidsGeom("#solid2");
        AssembledSolidResult assembled = new AssembledSolidResult("#2", solidWithVoids);

        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> service.generateMesh(assembled)
        );

        assertEquals("SolidWithVoidsGeom is not supported yet", ex.getMessage());
        assertEquals(0, tessellator.callCount());
        assertNull(tessellator.lastSolid());
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
}