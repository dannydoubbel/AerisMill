package be.doebi.aerismill.model.step.representation;

import be.doebi.aerismill.model.step.StepEntity;

import java.util.List;

public class AdvancedBrepShapeRepresentation extends StepEntity {
    private final String name;
    private final List<StepEntity> items;
    private final StepEntity contextOfItems;

    public AdvancedBrepShapeRepresentation(String id,
                                           String rawParameters,
                                           String name,
                                           List<StepEntity> items,
                                           StepEntity contextOfItems) {
        super(id, "ADVANCED_BREP_SHAPE_REPRESENTATION", rawParameters);
        this.name = name;
        this.items = items;
        this.contextOfItems = contextOfItems;
    }

    public String getName() {
        return name;
    }

    public List<StepEntity> getItems() {
        return items;
    }

    public StepEntity getContextOfItems() {
        return contextOfItems;
    }

    @Override
    public String toString() {
        return "AdvancedBrepShapeRepresentation{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", items=" + items +
                ", contextOfItems=" + contextOfItems +
                '}';
    }
}
