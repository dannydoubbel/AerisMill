package be.doebi.aerismill.model.step.definition;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

public class ShapeDefinitionRepresentation extends StepEntity {
    private final String definitionRef;
    private final String representationRef;

    public ShapeDefinitionRepresentation(String id,
                                         StepEntityType type,
                                         String rawParameters,
                                         String definitionRef,
                                         String representationRef) {
        super(id, type, rawParameters);
        this.definitionRef = definitionRef;
        this.representationRef = representationRef;
    }

    public String getDefinitionRef() {
        return definitionRef;
    }

    public String getRepresentationRef() {
        return representationRef;
    }

    @Override
    public String toString() {
        return "ShapeDefinitionRepresentation{" +
                "id='" + getId() + '\'' +
                ", definitionRef='" + definitionRef + '\'' +
                ", representationRef='" + representationRef + '\'' +
                '}';
    }
}