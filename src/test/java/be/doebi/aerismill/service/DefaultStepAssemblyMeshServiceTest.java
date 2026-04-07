package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.validate.geom.topology.ValidationReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultStepAssemblyMeshServiceTest {

    @Test
    void constructor_rejectsNullAssembledSolidMeshService() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> new DefaultStepAssemblyMeshService(null)
        );

        assertEquals("assembledSolidMeshService must not be null", ex.getMessage());
    }

    @Test
    void generateMesh_singleSolid_delegatesToAssembledSolidMeshService() {
        Mesh expected = mesh();
        StubAssembledSolidMeshService assembledSolidMeshService =
                new StubAssembledSolidMeshService(expected);

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        SolidGeom solid = solidGeom("#solid1");
        AssemblyResult assemblyResult = assemblyResult(
                List.of(solidAssemblyResult("#1", solid))
        );

        service.generateMesh(assemblyResult);

        assertEquals(1, assembledSolidMeshService.callCount());
        assertNotNull(assembledSolidMeshService.lastInput());
        assertEquals("#1", assembledSolidMeshService.lastInput().stepId());
        assertSame(solid, assembledSolidMeshService.lastInput().solid());
    }

    @Test
    void generateMesh_singleSolid_returnsMeshFromAssembledSolidMeshService() {
        Mesh expected = mesh();
        StubAssembledSolidMeshService assembledSolidMeshService =
                new StubAssembledSolidMeshService(expected);

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        SolidGeom solid = solidGeom("#solid1");
        AssemblyResult assemblyResult = assemblyResult(
                List.of(solidAssemblyResult("#1", solid))
        );

        Mesh result = service.generateMesh(assemblyResult);

        assertSame(expected, result);
    }

    @Test
    void generateMesh_noSolids_throwsIllegalArgumentException() {
        StubAssembledSolidMeshService assembledSolidMeshService =
                new StubAssembledSolidMeshService(mesh());

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        AssemblyResult assemblyResult = assemblyResult(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.generateMesh(assemblyResult)
        );

        assertEquals("AssemblyResult contains no solids", ex.getMessage());
        assertEquals(0, assembledSolidMeshService.callCount());
        assertNull(assembledSolidMeshService.lastInput());
    }

    @Test
    void generateMesh_multipleSolids_throwsUnsupportedOperationException() {
        StubAssembledSolidMeshService assembledSolidMeshService =
                new StubAssembledSolidMeshService(mesh());

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        AssemblyResult assemblyResult = assemblyResult(
                List.of(
                        solidAssemblyResult("#1", solidGeom("#solid1")),
                        solidAssemblyResult("#2", solidGeom("#solid2"))
                )
        );

        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> service.generateMesh(assemblyResult)
        );

        assertEquals("Multiple assembled solids are not supported yet", ex.getMessage());
        assertEquals(0, assembledSolidMeshService.callCount());
        assertNull(assembledSolidMeshService.lastInput());
    }

    @Test
    void generateMesh_nullInput_throwsNullPointerException() {
        StubAssembledSolidMeshService assembledSolidMeshService =
                new StubAssembledSolidMeshService(mesh());

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> service.generateMesh(null)
        );

        assertEquals("assemblyResult must not be null", ex.getMessage());
    }

    private AssemblyResult assemblyResult(List<SolidAssemblyResult> solids) {
        return new AssemblyResult(solids, List.of());
    }

    private SolidAssemblyResult solidAssemblyResult(String stepId, SolidGeom solid) {
        return new SolidAssemblyResult(stepId, solid, new ValidationReport());
    }

    private SolidGeom solidGeom(String stepId) {
        return new SolidGeom(stepId, new ShellGeom(stepId + "_outer", List.of()));
    }

    private Mesh mesh() {
        return new Mesh(List.of(), List.of());
    }

    private static final class StubAssembledSolidMeshService implements AssembledSolidMeshService {
        private final Mesh meshToReturn;
        private int callCount;
        private AssembledSolidResult lastInput;

        private StubAssembledSolidMeshService(Mesh meshToReturn) {
            this.meshToReturn = meshToReturn;
        }

        @Override
        public Mesh generateMesh(AssembledSolidResult assembledSolidResult) {
            callCount++;
            lastInput = assembledSolidResult;
            return meshToReturn;
        }

        private int callCount() {
            return callCount;
        }

        private AssembledSolidResult lastInput() {
            return lastInput;
        }
    }
}