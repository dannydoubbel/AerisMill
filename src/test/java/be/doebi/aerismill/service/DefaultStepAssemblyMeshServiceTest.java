package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.tessellation.shell.DebugSurfaceFamilyMeshes;
import be.doebi.aerismill.validate.geom.topology.ValidationReport;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Test
    void generateMesh_singleSolidWithVoids_delegatesToAssembledSolidMeshService() {
        Mesh expected = mesh();
        StubAssembledSolidMeshService assembledSolidMeshService =
                new StubAssembledSolidMeshService(expected);

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        SolidWithVoidsGeom solidWithVoids = solidWithVoidsGeom("#solid1");
        AssemblyResult assemblyResult = assemblyResult(
                List.of(solidWithVoidsAssemblyResult("#1", solidWithVoids))
        );

        service.generateMesh(assemblyResult);

        assertEquals(1, assembledSolidMeshService.callCount());
        assertNotNull(assembledSolidMeshService.lastInput());
        assertEquals("#1", assembledSolidMeshService.lastInput().stepId());
        assertSame(solidWithVoids, assembledSolidMeshService.lastInput().solid());
    }

    @Test
    void generateMesh_multipleSolids_returnsFirstPreviewableSolidMesh() {
        Mesh expected = mesh();

        StubAssembledSolidMeshService assembledSolidMeshService = new StubAssembledSolidMeshService();
        assembledSolidMeshService.willThrow(
                "#1",
                new IllegalArgumentException(
                        "No previewable faces found in shell. First reason: Face #100: only planar faces are supported for now."
                )
        );
        assembledSolidMeshService.willReturn("#2", expected);

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        AssemblyResult assemblyResult = assemblyResult(
                List.of(
                        solidAssemblyResult("#1", solidGeom("#solid1")),
                        solidAssemblyResult("#2", solidGeom("#solid2"))
                )
        );

        Mesh result = service.generateMesh(assemblyResult);

        assertSame(expected, result);
    }

    @Test
    void generateMesh_multipleSolids_skipsFailingFirstSolid() {
        Mesh expected = mesh();

        StubAssembledSolidMeshService assembledSolidMeshService = new StubAssembledSolidMeshService();
        assembledSolidMeshService.willThrow(
                "#1",
                new IllegalArgumentException(
                        "No previewable faces found in shell. First reason: Face #100: only planar faces are supported for now."
                )
        );
        assembledSolidMeshService.willReturn("#2", expected);

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        SolidGeom firstSolid = solidGeom("#solid1");
        SolidGeom secondSolid = solidGeom("#solid2");

        AssemblyResult assemblyResult = assemblyResult(
                List.of(
                        solidAssemblyResult("#1", firstSolid),
                        solidAssemblyResult("#2", secondSolid)
                )
        );

        service.generateMesh(assemblyResult);

        assertEquals(2, assembledSolidMeshService.callCount());
        assertEquals(2, assembledSolidMeshService.inputs().size());

        assertEquals("#1", assembledSolidMeshService.inputs().get(0).stepId());
        assertSame(firstSolid, assembledSolidMeshService.inputs().get(0).solid());

        assertEquals("#2", assembledSolidMeshService.inputs().get(1).stepId());
        assertSame(secondSolid, assembledSolidMeshService.inputs().get(1).solid());
    }

    @Test
    void generateMesh_multipleSolids_throwsWhenNoSolidIsPreviewable() {
        StubAssembledSolidMeshService assembledSolidMeshService = new StubAssembledSolidMeshService();
        assembledSolidMeshService.willThrow(
                "#1",
                new IllegalArgumentException(
                        "No previewable faces found in shell. First reason: Face #100: only planar faces are supported for now."
                )
        );
        assembledSolidMeshService.willThrow(
                "#2",
                new UnsupportedOperationException("SolidWithVoidsGeom is not supported yet")
        );

        DefaultStepAssemblyMeshService service =
                new DefaultStepAssemblyMeshService(assembledSolidMeshService);

        AssemblyResult assemblyResult = assemblyResult(
                List.of(
                        solidAssemblyResult("#1", solidGeom("#solid1")),
                        solidAssemblyResult("#2", solidGeom("#solid2"))
                )
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.generateMesh(assemblyResult)
        );

        assertEquals(
                "No previewable solids found in assembly. First reason: Solid #1: No previewable faces found in shell. First reason: Face #100: only planar faces are supported for now.",
                ex.getMessage()
        );

        assertEquals(2, assembledSolidMeshService.callCount());
    }

    private AssemblyResult assemblyResult(List<SolidAssemblyResult> solids) {
        return new AssemblyResult(solids, List.of());
    }

    private SolidAssemblyResult solidAssemblyResult(String stepId, SolidGeom solid) {
        return new SolidAssemblyResult(stepId, solid, new ValidationReport());
    }

    private SolidAssemblyResult solidWithVoidsAssemblyResult(String stepId, SolidWithVoidsGeom solidWithVoids) {
        return new SolidAssemblyResult(stepId, solidWithVoids, new ValidationReport());
    }

    private SolidGeom solidGeom(String stepId) {
        return new SolidGeom(stepId, new ShellGeom(stepId + "_outer", List.of()));
    }

    private SolidWithVoidsGeom solidWithVoidsGeom(String stepId) {
        return new SolidWithVoidsGeom(
                stepId,
                new ShellGeom(stepId + "_outer", List.of()),
                List.of(new ShellGeom(stepId + "_void1", List.of()))
        );
    }

    private Mesh mesh() {
        return new Mesh(List.of(), List.of());
    }

    private static final class StubAssembledSolidMeshService implements AssembledSolidMeshService {
        private final Mesh defaultMeshToReturn;
        private final List<AssembledSolidResult> inputs = new ArrayList<>();
        private final Map<String, Object> responsesByStepId = new HashMap<>();

        private StubAssembledSolidMeshService() {
            this.defaultMeshToReturn = null;
        }

        private StubAssembledSolidMeshService(Mesh defaultMeshToReturn) {
            this.defaultMeshToReturn = defaultMeshToReturn;
        }

        @Override
        public Mesh generateMesh(AssembledSolidResult assembledSolidResult) {
            inputs.add(assembledSolidResult);

            Object response = responsesByStepId.get(assembledSolidResult.stepId());

            if (response instanceof Mesh mesh) {
                return mesh;
            }
            if (response instanceof RuntimeException ex) {
                throw ex;
            }
            if (defaultMeshToReturn != null) {
                return defaultMeshToReturn;
            }

            throw new IllegalStateException("No stubbed response for stepId " + assembledSolidResult.stepId());
        }

        @Override
        public DebugSurfaceFamilyMeshes generateDebugSurfaceFamilyMeshes(AssembledSolidResult assembledSolidResult) {
            return null;
        }

        void willReturn(String stepId, Mesh mesh) {
            responsesByStepId.put(stepId, mesh);
        }

        void willThrow(String stepId, RuntimeException ex) {
            responsesByStepId.put(stepId, ex);
        }

        int callCount() {
            return inputs.size();
        }

        AssembledSolidResult lastInput() {
            return inputs.isEmpty() ? null : inputs.get(inputs.size() - 1);
        }

        List<AssembledSolidResult> inputs() {
            return inputs;
        }
    }
}