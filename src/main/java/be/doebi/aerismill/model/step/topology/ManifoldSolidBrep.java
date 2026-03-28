package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

public class ManifoldSolidBrep extends TopologyEntity {
    private final String name;
    private final String outerRef;

    private ClosedShell outer;

    public ManifoldSolidBrep(String id,
                             String rawParameters,
                             String name,
                             String outerRef) {
        super(id, StepEntityType.MANIFOLD_SOLID_BREP, rawParameters);
        this.name = name;
        this.outerRef = outerRef;
    }

    public String getName() {
        return name;
    }

    public String getOuterRef() {
        return outerRef;
    }

    public ClosedShell getOuter() {
        return outer;
    }

    @Override
    public void doResolve(StepModel model) {
        this.outer = outerRef == null ? null : model.resolveEntity(outerRef, ClosedShell.class);
    }

    @Override
    public String toString() {
        return "ManifoldSolidBrep{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", outerRef='" + outerRef + '\'' +
                ", outer=" + outer +
                '}';
    }
}