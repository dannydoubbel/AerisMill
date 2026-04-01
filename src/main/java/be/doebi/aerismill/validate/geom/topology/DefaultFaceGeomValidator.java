package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;

public class DefaultFaceGeomValidator implements FaceGeomValidator {

    private final DefaultLoopGeomValidator loopValidator;

    public DefaultFaceGeomValidator() {
        this.loopValidator = new DefaultLoopGeomValidator();
    }

    public DefaultFaceGeomValidator(DefaultLoopGeomValidator loopValidator) {
        this.loopValidator = loopValidator;
    }

    @Override
    public ValidationReport validate(FaceGeom face) {
        ValidationReport report = new ValidationReport();

        if (face == null) {
            report.addError(
                    ValidationCode.FACE_NULL,
                    null,
                    "FaceGeom is null"
            );
            return report;
        }

        if (face.bounds() == null || face.bounds().isEmpty()) {
            report.addError(
                    ValidationCode.FACE_NO_BOUNDS,
                    face.stepId(),
                    "FaceGeom must contain at least one bound"
            );
            return report;
        }

        for (int i = 0; i < face.bounds().size(); i++) {
            LoopGeom bound = face.bounds().get(i);

            if (bound == null) {
                report.addError(
                        ValidationCode.FACE_BOUND_NULL,
                        face.stepId(),
                        "FaceGeom contains a null bound at index " + i
                );
                continue;
            }

            ValidationReport loopReport = loopValidator.validate(bound);

            for (ValidationMessage message : loopReport.messages()) {
                report.addMessage(message);
            }
        }

        return report;
    }
}