package be.doebi.aerismill.model.step.presentation;

import be.doebi.aerismill.model.step.StepEntity;

public abstract class PresentationStepEntity extends StepEntity {

    protected PresentationStepEntity(StepEntity entity) {
        super(entity.getId(), entity.getType(), entity.getRawParameters());
    }
}