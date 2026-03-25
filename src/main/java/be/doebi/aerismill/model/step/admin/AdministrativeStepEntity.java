package be.doebi.aerismill.model.step.admin;

import be.doebi.aerismill.model.step.StepEntity;


public abstract class AdministrativeStepEntity extends StepEntity {
    protected AdministrativeStepEntity(StepEntity entity) {
        super(entity.getId(), entity.getType(), entity.getRawParameters());
    }
}
