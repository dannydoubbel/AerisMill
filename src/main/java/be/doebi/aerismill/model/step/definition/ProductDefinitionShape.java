package be.doebi.aerismill.model.step.definition;

import be.doebi.aerismill.model.step.base.StepEntity;

public class ProductDefinitionShape extends StepEntity {
    private final String name;
    private final String description;
    private final String definitionRef;

    public ProductDefinitionShape(String id,
                                  String type,
                                  String rawParameters,
                                  String name,
                                  String description,
                                  String definitionRef) {
        super(id, type, rawParameters);
        this.name = name;
        this.description = description;
        this.definitionRef = definitionRef;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDefinitionRef() {
        return definitionRef;
    }

    @Override
    public String toString() {
        return "ProductDefinitionShape{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", definitionRef='" + definitionRef + '\'' +
                '}';
    }
}