package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;

public class DefaultShellGeomValidator implements ShellGeomValidator {

    private final FaceGeomValidator faceValidator;

    public DefaultShellGeomValidator() {
        this.faceValidator = new DefaultFaceGeomValidator();
    }

    public DefaultShellGeomValidator(FaceGeomValidator faceValidator) {
        this.faceValidator = faceValidator;
    }

    @Override
    public ValidationReport validate(ShellGeom shell) {
        ValidationReport report = new ValidationReport();

        if (shell == null) {
            report.addError(
                    ValidationCode.SHELL_NULL,
                    null,
                    "ShellGeom is null"
            );
            return report;
        }

        if (shell.faces() == null || shell.faces().isEmpty()) {
            report.addError(
                    ValidationCode.SHELL_EMPTY,
                    shell.stepId(),
                    "ShellGeom must contain at least one face"
            );
            return report;
        }

        for (int i = 0; i < shell.faces().size(); i++) {
            FaceGeom face = shell.faces().get(i);

            if (face == null) {
                report.addError(
                        ValidationCode.SHELL_FACE_NULL,
                        shell.stepId(),
                        "ShellGeom contains a null face at index " + i
                );
                continue;
            }

            ValidationReport faceReport = faceValidator.validate(face);
            for (ValidationMessage message : faceReport.messages()) {
                report.addMessage(message);
            }
        }

        return report;
    }
}