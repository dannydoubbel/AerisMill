package be.doebi.aerismill.model.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

public abstract class TopologyEntity extends StepEntity {
    protected TopologyEntity(String id, StepEntityType type, String rawParameters) {
        super(id, type, rawParameters);
    }
}
