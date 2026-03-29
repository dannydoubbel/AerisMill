package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

import java.util.List;

public class BrepWithVoids extends TopologyEntity {
    private final String name;
    private final String outerRef;
    private final List<String> voidRefs;

    private ClosedShell outer;
    private List<ClosedShell> voids;

    public BrepWithVoids(String id,
                         String rawParameters,
                         String name,
                         String outerRef,
                         List<String> voidRefs) {
        super(id, StepEntityType.BREP_WITH_VOIDS, rawParameters);
        this.name = name;
        this.outerRef = outerRef;
        this.voidRefs = voidRefs;
    }

    public String getName() {
        return name;
    }

    public String getOuterRef() {
        return outerRef;
    }

    public List<String> getVoidRefs() {
        return voidRefs;
    }

    public ClosedShell getOuter() {
        return outer;
    }

    public List<ClosedShell> getVoids() {
        return voids;
    }

    @Override
    public void doResolve(StepModel model) {
        this.outer = outerRef == null ? null : model.resolveEntity(outerRef, ClosedShell.class);
        this.voids = model.resolveEntityList(voidRefs, ClosedShell.class);
    }

    @Override
    public String toString() {
        return "BrepWithVoids{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", outerRef='" + outerRef + '\'' +
                ", voidRefs=" + voidRefs +
                ", outer=" + outer +
                ", voids=" + voids +
                '}';
    }
}