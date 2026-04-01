package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.LoopGeom;

public final class LoopGeomValidator {

    public ValidationReport validate(LoopGeom loop) {
        ValidationReport report = new ValidationReport();

        if (loop == null) {
            report.addError(
                    ValidationCode.LOOP_NULL,
                    null,
                    "LoopGeom is null"
            );
            return report;
        }

        if (loop.edges() == null || loop.edges().isEmpty()) {
            report.addError(
                    ValidationCode.LOOP_EMPTY,
                    loop.stepId(),
                    "LoopGeom must contain at least one oriented edge"
            );
            return report;
        }

        return report;
    }
}