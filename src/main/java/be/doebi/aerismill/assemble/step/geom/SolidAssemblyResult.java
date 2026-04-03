package be.doebi.aerismill.assemble.step.geom;

import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.validate.geom.topology.ValidationReport;


public record SolidAssemblyResult(
        String stepId,
        SolidGeom solid,
        SolidWithVoidsGeom solidWithVoids,
        ValidationReport validationReport
) {
    public SolidAssemblyResult(String stepId, SolidGeom solid, ValidationReport validationReport) {
        this(stepId, solid, null, validationReport);
    }

    public SolidAssemblyResult(String stepId, SolidWithVoidsGeom solidWithVoids, ValidationReport validationReport) {
        this(stepId, null, solidWithVoids, validationReport);
    }
}
