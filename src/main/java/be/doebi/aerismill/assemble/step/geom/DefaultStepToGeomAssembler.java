package be.doebi.aerismill.assemble.step.geom;

import be.doebi.aerismill.eval.step.topology.SolidGeomEvaluator;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.validate.geom.topology.TopologyValidationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

        return new AssemblyResult(solids, issues);
    }
}