package be.doebi.aerismill.model.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

public abstract class GeometricEntity extends StepEntity {
    protected GeometricEntity(String id, StepEntityType type, String rawParameters) {
        super(id, type, rawParameters);
    }
}
