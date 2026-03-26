package be.doebi.aerismill.model.step;

import be.doebi.aerismill.model.step.base.StepEntity;

public abstract class GeometricEntity extends StepEntity {
    protected GeometricEntity(String id, String type, String rawParameters) {
        super(id, type, rawParameters);
    }
}
