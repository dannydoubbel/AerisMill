package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.SolidGeom;

public class DefaultSolidGeomValidator implements SolidGeomValidator {

    private final ShellGeomValidator shellValidator;

    public DefaultSolidGeomValidator() {
        this.shellValidator = new DefaultShellGeomValidator();
    }

    public DefaultSolidGeomValidator(ShellGeomValidator shellValidator) {
        this.shellValidator = shellValidator;
    }

    @Override
    public ValidationReport validate(SolidGeom solid) {
        ValidationReport report = new ValidationReport();

        if (solid == null) {
            report.addError(
                    ValidationCode.SOLID_NULL,
                    null,
                    "SolidGeom is null"
            );
            return report;
        }

        if (solid.outerShell() == null) {
            report.addError(
                    ValidationCode.SOLID_OUTER_SHELL_NULL,
                    solid.stepId(),
                    "SolidGeom must contain a non-null outer shell"
            );
            return report;
        }

        ValidationReport shellReport = shellValidator.validate(solid.outerShell());
        report.addAll(shellReport);

        return report;
    }
}