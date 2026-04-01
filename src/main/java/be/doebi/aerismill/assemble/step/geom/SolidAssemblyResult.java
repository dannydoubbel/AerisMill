package be.doebi.aerismill.assemble.step.geom;

import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.validate.geom.topology.ValidationReport;


public record SolidAssemblyResult(
        String stepId,
        SolidGeom solid,
        ValidationReport validationReport
) {}