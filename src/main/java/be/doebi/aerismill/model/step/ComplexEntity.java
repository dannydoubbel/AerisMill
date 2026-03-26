package be.doebi.aerismill.model.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

import java.util.List;

public class ComplexEntity extends StepEntity {
    private final List<ComplexEntityPart> parts;

    public ComplexEntity(String id, String rawParameters, List<ComplexEntityPart> parts) {
        super(id, StepEntityType.COMPLEX_ENTITY.getName(), rawParameters);
        this.parts = parts;
    }

    public List<ComplexEntityPart> getParts() {
        return parts;
    }
}
