package be.doebi.aerismill.assemble.step.geom;

import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.topology.BrepWithVoids;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;
import be.doebi.aerismill.validate.geom.topology.*;

import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.validate.geom.topology.TopologyValidationService;
import be.doebi.aerismill.validate.geom.topology.ValidationCode;
import be.doebi.aerismill.validate.geom.topology.ValidationReport;
import org.junit.jupiter.api.Test;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;


class DefaultStepToGeomAssemblerTest {

    @Test
    void assemble_nullStepModel_returnsErrorIssue() {
        DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(null, null);

        AssemblyResult result = assembler.assemble(null);

        assertNotNull(result);
        assertNotNull(result.solids());
        assertNotNull(result.issues());
        assertEquals(0, result.solids().size());
        assertEquals(1, result.issues().size());

        AssemblyIssue issue = result.issues().getFirst();
        assertNull(issue.stepId());
        assertEquals(AssemblyIssueSeverity.ERROR, issue.severity());
        assertEquals(AssemblyIssueCode.NULL_STEP_MODEL, issue.code());
        assertEquals("StepModel must not be null", issue.message());
    }










        @Test
        void assemble_emptyStepModel_returnsNoSupportedRootsFound() {
            StepModel stepModel = new StepModel();

            DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(
                    manifoldSolidBrep -> {
                        fail("Solid evaluator should not be called");
                        return null;
                    },
                    new NoOpTopologyValidationService()
            );

            AssemblyResult result = assembler.assemble(stepModel);

            assertNotNull(result);
            assertNotNull(result.solids());
            assertNotNull(result.issues());
            assertEquals(0, result.solids().size());
            assertEquals(1, result.issues().size());

            AssemblyIssue issue = result.issues().getFirst();
            assertNull(issue.stepId());
            assertEquals(AssemblyIssueSeverity.INFO, issue.severity());
            assertEquals(AssemblyIssueCode.NO_SUPPORTED_ROOTS_FOUND, issue.code());
            assertEquals("No supported solid roots were assembled from StepModel", issue.message());
        }

        @Test
        void assemble_brepWithVoids_reportsUnsupportedRootType_whenNoEvaluatorProvided() {
            StepModel stepModel = new StepModel();
            stepModel.addEntity(new BrepWithVoids(
                    "#10",
                    "( 'test', #20, ( #30 ) )",
                    "test",
                    "#20",
                    List.of("#30")
            ));

            DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(
                    manifoldSolidBrep -> {
                        fail("Solid evaluator should not be called");
                        return null;
                    },
                    new NoOpTopologyValidationService()
            );

            AssemblyResult result = assembler.assemble(stepModel);

            assertNotNull(result);
            assertEquals(0, result.solids().size());
            assertEquals(2, result.issues().size());

            AssemblyIssue unsupported = result.issues().stream()
                    .filter(issue -> issue.code() == AssemblyIssueCode.UNSUPPORTED_ROOT_TYPE)
                    .findFirst()
                    .orElseThrow();

            assertEquals("#10", unsupported.stepId());
            assertEquals(AssemblyIssueSeverity.INFO, unsupported.severity());
            assertEquals("BREP_WITH_VOIDS is not yet supported by the assembler", unsupported.message());

            AssemblyIssue noSupportedRoots = result.issues().stream()
                    .filter(issue -> issue.code() == AssemblyIssueCode.NO_SUPPORTED_ROOTS_FOUND)
                    .findFirst()
                    .orElseThrow();

            assertNull(noSupportedRoots.stepId());
            assertEquals(AssemblyIssueSeverity.INFO, noSupportedRoots.severity());
        }

        @Test
        void assemble_brepWithVoids_addsSolidAssemblyResult() {
            StepModel stepModel = new StepModel();
            BrepWithVoids brep = new BrepWithVoids(
                    "#110",
                    "( 'solid', #210, ( #310 ) )",
                    "solid",
                    "#210",
                    List.of("#310")
            );
            stepModel.addEntity(brep);

            SolidWithVoidsGeom expectedSolid = new SolidWithVoidsGeom(
                    "#110",
                    new ShellGeom("#210", List.of()),
                    List.of(new ShellGeom("#310", List.of()))
            );

            DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(
                    manifoldSolidBrep -> {
                        fail("Manifold solid evaluator should not be called");
                        return null;
                    },
                    brepWithVoids -> expectedSolid,
                    new NoOpTopologyValidationService()
            );

            AssemblyResult result = assembler.assemble(stepModel);

            assertNotNull(result);
            assertEquals(1, result.solids().size());
            assertEquals(0, result.issues().size());

            SolidAssemblyResult solidResult = result.solids().getFirst();
            assertEquals("#110", solidResult.stepId());
            assertNull(solidResult.solid());
            assertSame(expectedSolid, solidResult.solidWithVoids());
            assertNotNull(solidResult.validationReport());
            assertEquals(0, solidResult.validationReport().errorCount());
        }

        @Test
        void assemble_manifoldSolidBrep_addsSolidAssemblyResult() {
            StepModel stepModel = new StepModel();
            ManifoldSolidBrep brep = new ManifoldSolidBrep(
                    "#100",
                    "( 'solid', #200 )",
                    "solid",
                    "#200"
            );
            stepModel.addEntity(brep);

            SolidGeom expectedSolid = new SolidGeom("#100", new ShellGeom("#200", List.of()));

            DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(
                    manifoldSolidBrep -> expectedSolid,
                    new TopologyValidationService() {
                        @Override
                        public ValidationReport validateLoop(be.doebi.aerismill.model.geom.topology.LoopGeom loopGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateFace(be.doebi.aerismill.model.geom.topology.FaceGeom faceGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateShell(be.doebi.aerismill.model.geom.topology.ShellGeom shellGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateSolid(be.doebi.aerismill.model.geom.topology.SolidGeom solidGeom) {
                            return emptyReport();
                        }
                    }
            );

            AssemblyResult result = assembler.assemble(stepModel);

            assertNotNull(result);
            assertEquals(1, result.solids().size());
            assertEquals(0, result.issues().size());

            SolidAssemblyResult solidResult = result.solids().getFirst();
            assertEquals("#100", solidResult.stepId());
            assertSame(expectedSolid, solidResult.solid());
            assertNotNull(solidResult.validationReport());
            assertEquals(0, solidResult.validationReport().errorCount());
        }

        @Test
        void assemble_manifoldSolidBrep_withValidationErrors_addsValidationFailedIssue() {
            StepModel stepModel = new StepModel();
            ManifoldSolidBrep brep = new ManifoldSolidBrep(
                    "#101",
                    "( 'solid', #201 )",
                    "solid",
                    "#201"
            );
            stepModel.addEntity(brep);

            SolidGeom expectedSolid = new SolidGeom("#101", new ShellGeom("#201", List.of()));

            DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(
                    manifoldSolidBrep -> expectedSolid,
                    new TopologyValidationService() {
                        @Override
                        public ValidationReport validateLoop(be.doebi.aerismill.model.geom.topology.LoopGeom loopGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateFace(be.doebi.aerismill.model.geom.topology.FaceGeom faceGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateShell(be.doebi.aerismill.model.geom.topology.ShellGeom shellGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateSolid(be.doebi.aerismill.model.geom.topology.SolidGeom solidGeom) {
                            return reportWithError();
                        }
                    }
            );

            AssemblyResult result = assembler.assemble(stepModel);

            assertNotNull(result);
            assertEquals(1, result.solids().size());
            assertEquals(1, result.issues().size());

            AssemblyIssue issue = result.issues().getFirst();
            assertEquals("#101", issue.stepId());
            assertEquals(AssemblyIssueSeverity.WARNING, issue.severity());
            assertEquals(AssemblyIssueCode.VALIDATION_FAILED, issue.code());
            assertEquals("Assembled solid has topology validation errors", issue.message());
        }

        @Test
        void assemble_manifoldSolidBrep_whenEvaluatorThrows_addsEvaluationFailedIssue() {
            StepModel stepModel = new StepModel();
            ManifoldSolidBrep brep = new ManifoldSolidBrep(
                    "#102",
                    "( 'solid', #202 )",
                    "solid",
                    "#202"
            );
            stepModel.addEntity(brep);

            DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(
                    manifoldSolidBrep -> {
                        throw new IllegalStateException("boom");
                    },
                    new NoOpTopologyValidationService()
            );

            AssemblyResult result = assembler.assemble(stepModel);

            assertNotNull(result);
            assertEquals(0, result.solids().size());
            assertEquals(2, result.issues().size());

            AssemblyIssue evalFailed = result.issues().stream()
                    .filter(issue -> issue.code() == AssemblyIssueCode.EVALUATION_FAILED)
                    .findFirst()
                    .orElseThrow();

            assertEquals("#102", evalFailed.stepId());
            assertEquals(AssemblyIssueSeverity.ERROR, evalFailed.severity());
            assertEquals("Failed to assemble MANIFOLD_SOLID_BREP: boom", evalFailed.message());

            AssemblyIssue noSupportedRoots = result.issues().stream()
                    .filter(issue -> issue.code() == AssemblyIssueCode.NO_SUPPORTED_ROOTS_FOUND)
                    .findFirst()
                    .orElseThrow();

            assertNull(noSupportedRoots.stepId());
            assertEquals(AssemblyIssueSeverity.INFO, noSupportedRoots.severity());
        }

        @Test
        void assemble_brepWithVoids_whenEvaluatorThrows_addsEvaluationFailedIssue() {
            StepModel stepModel = new StepModel();
            BrepWithVoids brep = new BrepWithVoids(
                    "#111",
                    "( 'solid', #211, ( #311 ) )",
                    "solid",
                    "#211",
                    List.of("#311")
            );
            stepModel.addEntity(brep);

            DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(
                    manifoldSolidBrep -> {
                        fail("Manifold solid evaluator should not be called");
                        return null;
                    },
                    brepWithVoids -> {
                        throw new IllegalStateException("boom");
                    },
                    new NoOpTopologyValidationService()
            );

            AssemblyResult result = assembler.assemble(stepModel);

            assertNotNull(result);
            assertEquals(0, result.solids().size());
            assertEquals(2, result.issues().size());

            AssemblyIssue evalFailed = result.issues().stream()
                    .filter(issue -> issue.code() == AssemblyIssueCode.EVALUATION_FAILED)
                    .findFirst()
                    .orElseThrow();

            assertEquals("#111", evalFailed.stepId());
            assertEquals(AssemblyIssueSeverity.ERROR, evalFailed.severity());
            assertEquals("Failed to assemble BREP_WITH_VOIDS: boom", evalFailed.message());

            AssemblyIssue noSupportedRoots = result.issues().stream()
                    .filter(issue -> issue.code() == AssemblyIssueCode.NO_SUPPORTED_ROOTS_FOUND)
                    .findFirst()
                    .orElseThrow();

            assertNull(noSupportedRoots.stepId());
            assertEquals(AssemblyIssueSeverity.INFO, noSupportedRoots.severity());
        }

        @Test
        void assemble_brepWithVoids_withValidationErrors_addsValidationFailedIssue() {
            StepModel stepModel = new StepModel();
            BrepWithVoids brep = new BrepWithVoids(
                    "#112",
                    "( 'solid', #212, ( #312 ) )",
                    "solid",
                    "#212",
                    List.of("#312")
            );
            stepModel.addEntity(brep);

            SolidWithVoidsGeom expectedSolid = new SolidWithVoidsGeom(
                    "#112",
                    new ShellGeom("#212", List.of()),
                    List.of(new ShellGeom("#312", List.of()))
            );

            DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(
                    manifoldSolidBrep -> {
                        fail("Manifold solid evaluator should not be called");
                        return null;
                    },
                    brepWithVoids -> expectedSolid,
                    new TopologyValidationService() {
                        @Override
                        public ValidationReport validateLoop(be.doebi.aerismill.model.geom.topology.LoopGeom loopGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateFace(be.doebi.aerismill.model.geom.topology.FaceGeom faceGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateShell(be.doebi.aerismill.model.geom.topology.ShellGeom shellGeom) {
                            return emptyReport();
                        }

                        @Override
                        public ValidationReport validateSolid(be.doebi.aerismill.model.geom.topology.SolidGeom solidGeom) {
                            return reportWithError();
                        }
                    }
            );

            AssemblyResult result = assembler.assemble(stepModel);

            assertNotNull(result);
            assertEquals(1, result.solids().size());
            assertEquals(1, result.issues().size());

            SolidAssemblyResult solidResult = result.solids().getFirst();
            assertNull(solidResult.solid());
            assertSame(expectedSolid, solidResult.solidWithVoids());

            AssemblyIssue issue = result.issues().getFirst();
            assertEquals("#112", issue.stepId());
            assertEquals(AssemblyIssueSeverity.WARNING, issue.severity());
            assertEquals(AssemblyIssueCode.VALIDATION_FAILED, issue.code());
            assertEquals("Assembled solid has topology validation errors", issue.message());
        }

        private static ValidationReport emptyReport() {
            return new ValidationReport();
        }

    private static ValidationReport reportWithError() {
        ValidationReport report = new ValidationReport();
        report.addError(
                ValidationCode.SHELL_HAS_NO_FACES,
                "#test",
                "test error"
        );
        return report;
    }

        private static class NoOpTopologyValidationService implements TopologyValidationService {
            @Override
            public ValidationReport validateLoop(be.doebi.aerismill.model.geom.topology.LoopGeom loopGeom) {
                return emptyReport();
            }

            @Override
            public ValidationReport validateFace(be.doebi.aerismill.model.geom.topology.FaceGeom faceGeom) {
                return emptyReport();
            }

            @Override
            public ValidationReport validateShell(be.doebi.aerismill.model.geom.topology.ShellGeom shellGeom) {
                return emptyReport();
            }

            @Override
            public ValidationReport validateSolid(be.doebi.aerismill.model.geom.topology.SolidGeom solidGeom) {
                return emptyReport();
            }
        }

}
