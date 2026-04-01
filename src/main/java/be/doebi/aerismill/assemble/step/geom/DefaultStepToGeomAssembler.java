package be.doebi.aerismill.assemble.step.geom;

import be.doebi.aerismill.eval.step.topology.SolidGeomEvaluator;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.topology.BrepWithVoids;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;
import be.doebi.aerismill.validate.geom.topology.TopologyValidationService;
import be.doebi.aerismill.validate.geom.topology.ValidationReport;

import java.util.ArrayList;
import java.util.List;

public class DefaultStepToGeomAssembler implements StepToGeomAssembler {

    private final SolidGeomEvaluator solidGeomEvaluator;
    private final TopologyValidationService topologyValidationService;

    public DefaultStepToGeomAssembler(
            SolidGeomEvaluator solidGeomEvaluator,
            TopologyValidationService topologyValidationService
    ) {
        this.solidGeomEvaluator = solidGeomEvaluator;
        this.topologyValidationService = topologyValidationService;
    }

    @Override
    public AssemblyResult assemble(StepModel stepModel) {
        List<SolidAssemblyResult> solids = new ArrayList<>();
        List<AssemblyIssue> issues = new ArrayList<>();

        if (stepModel == null) {
            issues.add(new AssemblyIssue(
                    null,
                    AssemblyIssueSeverity.ERROR,
                    AssemblyIssueCode.NULL_STEP_MODEL,
                    "StepModel must not be null"
            ));
            return new AssemblyResult(solids, issues);
        }

        for (StepEntity entity : stepModel.getEntities()) {
            if (entity instanceof ManifoldSolidBrep manifoldSolidBrep) {
                assembleManifoldSolidBrep(manifoldSolidBrep, solids, issues);
                continue;
            }

            if (entity instanceof BrepWithVoids brepWithVoids) {
                issues.add(new AssemblyIssue(
                        brepWithVoids.getId(),
                        AssemblyIssueSeverity.INFO,
                        AssemblyIssueCode.UNSUPPORTED_ROOT_TYPE,
                        "BREP_WITH_VOIDS is not yet supported by the assembler"
                ));
            }
        }

        if (solids.isEmpty()) {
            issues.add(new AssemblyIssue(
                    null,
                    AssemblyIssueSeverity.INFO,
                    AssemblyIssueCode.NO_SUPPORTED_ROOTS_FOUND,
                    "No supported solid roots were assembled from StepModel"
            ));
        }

        return new AssemblyResult(solids, issues);
    }

    private void assembleManifoldSolidBrep(
            ManifoldSolidBrep manifoldSolidBrep,
            List<SolidAssemblyResult> solids,
            List<AssemblyIssue> issues
    ) {
        try {
            SolidGeom solid = solidGeomEvaluator.evaluateSolid(manifoldSolidBrep);
            ValidationReport validationReport = topologyValidationService.validateSolid(solid);

            solids.add(new SolidAssemblyResult(
                    manifoldSolidBrep.getId(),
                    solid,
                    validationReport
            ));

            if (validationReport.errorCount() > 0) {
                issues.add(new AssemblyIssue(
                        manifoldSolidBrep.getId(),
                        AssemblyIssueSeverity.WARNING,
                        AssemblyIssueCode.VALIDATION_FAILED,
                        "Assembled solid has topology validation errors"
                ));
            }

        } catch (Exception e) {
            issues.add(new AssemblyIssue(
                    manifoldSolidBrep.getId(),
                    AssemblyIssueSeverity.ERROR,
                    AssemblyIssueCode.EVALUATION_FAILED,
                    "Failed to assemble MANIFOLD_SOLID_BREP: " + e.getMessage()
            ));
        }
    }
}