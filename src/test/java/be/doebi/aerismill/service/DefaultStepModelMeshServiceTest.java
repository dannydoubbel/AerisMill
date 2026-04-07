package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.validate.geom.topology.ValidationReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultStepModelMeshServiceTest {

    @Test
    void constructor_rejectsNullStepAssemblyService() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> new DefaultStepModelMeshService(null, new StubStepAssemblyMeshService(mesh()))
        );

        assertEquals("stepAssemblyService must not be null", ex.getMessage());
    }

    @Test
    void constructor_rejectsNullStepAssemblyMeshService() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> new DefaultStepModelMeshService(new StubStepAssemblyService(assemblyResult()), null)
        );

        assertEquals("stepAssemblyMeshService must not be null", ex.getMessage());
    }

    @Test
    void generateMesh_delegatesToStepAssemblyService() {
        StepModel stepModel = stepModel();

        StubStepAssemblyService stepAssemblyService = new StubStepAssemblyService(assemblyResult());
        StubStepAssemblyMeshService stepAssemblyMeshService = new StubStepAssemblyMeshService(mesh());

        DefaultStepModelMeshService service =
                new DefaultStepModelMeshService(stepAssemblyService, stepAssemblyMeshService);

        service.generateMesh(stepModel);

        assertEquals(1, stepAssemblyService.callCount());
        assertSame(stepModel, stepAssemblyService.lastInput());
    }

    @Test
    void generateMesh_delegatesToStepAssemblyMeshService() {
        StepModel stepModel = stepModel();
        AssemblyResult assemblyResult = assemblyResult();

        StubStepAssemblyService stepAssemblyService = new StubStepAssemblyService(assemblyResult);
        StubStepAssemblyMeshService stepAssemblyMeshService = new StubStepAssemblyMeshService(mesh());

        DefaultStepModelMeshService service =
                new DefaultStepModelMeshService(stepAssemblyService, stepAssemblyMeshService);

        service.generateMesh(stepModel);

        assertEquals(1, stepAssemblyMeshService.callCount());
        assertSame(assemblyResult, stepAssemblyMeshService.lastInput());
    }

    @Test
    void generateMesh_returnsMeshFromStepAssemblyMeshService() {
        StepModel stepModel = stepModel();
        Mesh expected = mesh();

        StubStepAssemblyService stepAssemblyService = new StubStepAssemblyService(assemblyResult());
        StubStepAssemblyMeshService stepAssemblyMeshService = new StubStepAssemblyMeshService(expected);

        DefaultStepModelMeshService service =
                new DefaultStepModelMeshService(stepAssemblyService, stepAssemblyMeshService);

        Mesh result = service.generateMesh(stepModel);

        assertSame(expected, result);
    }

    @Test
    void generateMesh_nullInput_throwsNullPointerException() {
        StubStepAssemblyService stepAssemblyService = new StubStepAssemblyService(assemblyResult());
        StubStepAssemblyMeshService stepAssemblyMeshService = new StubStepAssemblyMeshService(mesh());

        DefaultStepModelMeshService service =
                new DefaultStepModelMeshService(stepAssemblyService, stepAssemblyMeshService);

        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> service.generateMesh(null)
        );

        assertEquals("stepModel must not be null", ex.getMessage());
    }

    private StepModel stepModel() {
        // Adjust this one helper to your actual StepModel constructor shape.
        return new StepModel();
    }

    private AssemblyResult assemblyResult() {
        return new AssemblyResult(
                List.of(new SolidAssemblyResult("#1", solidGeom("#solid1"), new ValidationReport())),
                List.of()
        );
    }

    private SolidGeom solidGeom(String stepId) {
        return new SolidGeom(stepId, new ShellGeom(stepId + "_outer", List.of()));
    }

    private Mesh mesh() {
        return new Mesh(List.of(), List.of());
    }

    private static final class StubStepAssemblyService extends StepAssemblyService {
        private final AssemblyResult resultToReturn;
        private int callCount;
        private StepModel lastInput;

        private StubStepAssemblyService(AssemblyResult resultToReturn) {
            this.resultToReturn = resultToReturn;
        }

        @Override
        public AssemblyResult assemble(StepModel stepModel) {
            callCount++;
            lastInput = stepModel;
            return resultToReturn;
        }

        private int callCount() {
            return callCount;
        }

        private StepModel lastInput() {
            return lastInput;
        }
    }

    private static final class StubStepAssemblyMeshService implements StepAssemblyMeshService {
        private final Mesh meshToReturn;
        private int callCount;
        private AssemblyResult lastInput;

        private StubStepAssemblyMeshService(Mesh meshToReturn) {
            this.meshToReturn = meshToReturn;
        }

        @Override
        public Mesh generateMesh(AssemblyResult assemblyResult) {
            callCount++;
            lastInput = assemblyResult;
            return meshToReturn;
        }

        private int callCount() {
            return callCount;
        }

        private AssemblyResult lastInput() {
            return lastInput;
        }
    }
}