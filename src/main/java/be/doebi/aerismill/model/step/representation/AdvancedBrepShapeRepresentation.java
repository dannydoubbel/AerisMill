package be.doebi.aerismill.model.step.representation;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.topology.ClosedShell;
import be.doebi.aerismill.model.step.topology.OrientedClosedShell;

import java.util.ArrayList;
import java.util.List;

public class AdvancedBrepShapeRepresentation extends ResolvableStepEntity {
    private final String name;
    private final List<String> itemRefs;
    private final String contextOfItemsRef;

    private List<StepEntity> items;
    private StepEntity contextOfItems;

    public AdvancedBrepShapeRepresentation(String id,
                                           String rawParameters,
                                           String name,
                                           List<String> itemRefs,
                                           String contextOfItemsRef) {
        super(id, StepEntityType.ADVANCED_BREP_SHAPE_REPRESENTATION, rawParameters);
        this.name = name;
        this.itemRefs = itemRefs;
        this.contextOfItemsRef = contextOfItemsRef;
    }

    public String getName() {
        return name;
    }

    public List<String> getItemRefs() {
        return itemRefs;
    }

    public String getContextOfItemsRef() {
        return contextOfItemsRef;
    }

    public List<StepEntity> getItems() {
        return items;
    }

    public StepEntity getContextOfItems() {
        return contextOfItems;
    }

    public List<ClosedShell> getClosedShellItems() {
        List<ClosedShell> result = new ArrayList<>();

        for (StepEntity item : items) {
            if (item instanceof ClosedShell closedShell) {
                result.add(closedShell);
            } else if (item instanceof OrientedClosedShell orientedClosedShell && orientedClosedShell.getClosedShell() != null) {
                result.add(orientedClosedShell.getClosedShell());
            }
        }

        return result;
    }



    @Override
    public void doResolve(StepModel model) {
        this.items = model.resolveEntityList(itemRefs, StepEntity.class);
        this.contextOfItems = contextOfItemsRef == null ? null : model.resolveEntity(contextOfItemsRef, StepEntity.class);
    }

    @Override
    public String toString() {
        return "AdvancedBrepShapeRepresentation{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", itemRefs=" + itemRefs +
                ", contextOfItemsRef='" + contextOfItemsRef + '\'' +
                ", items=" + items +
                ", contextOfItems=" + contextOfItems +
                '}';
    }
}