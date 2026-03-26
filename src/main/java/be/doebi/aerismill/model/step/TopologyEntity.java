package be.doebi.aerismill.model.step;

import be.doebi.aerismill.model.step.base.StepEntity;

public abstract class TopologyEntity extends StepEntity {
    protected TopologyEntity(String id, String type, String rawParameters) {
        super(id, type, rawParameters);
    }
}
