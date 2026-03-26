package be.doebi.aerismill.model.step.representation;

import be.doebi.aerismill.model.step.base.StepEntity;

import java.util.List;

public class ShapeRepresentation extends StepEntity {
    private final String name;
    private final List<String> itemRefs;
    private final String contextRef;

    public ShapeRepresentation(String id,
                               String type,
                               String rawParameters,
                               String name,
                               List<String> itemRefs,
                               String contextRef) {
        super(id, type, rawParameters);
        this.name = name;
        this.itemRefs = itemRefs;
        this.contextRef = contextRef;
    }

    public String getName() {
        return name;
    }

    public List<String> getItemRefs() {
        return itemRefs;
    }

    public String getContextRef() {
        return contextRef;
    }

    @Override
    public String toString() {
        return "ShapeRepresentation{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", itemRefs=" + itemRefs +
                ", contextRef='" + contextRef + '\'' +
                '}';
    }
}