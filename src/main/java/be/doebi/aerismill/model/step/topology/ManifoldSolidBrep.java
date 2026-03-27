package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

public class ManifoldSolidBrep extends TopologyEntity {
    private final String name;
    private final ClosedShell outer;

    public ManifoldSolidBrep(String id,
                             String rawParameters,
                             String name,
                             ClosedShell outer) {
        super(id, StepEntityType.MANIFOLD_SOLID_BREP, rawParameters);
        this.name = name;
        this.outer = outer;
    }

    public String getName() {
        return name;
    }

    public ClosedShell getOuter() {
        return outer;
    }

    @Override
    protected void doResolve(StepModel model) {
        // resolve refs here
    }

    @Override
    public String toString() {
        return "ManifoldSolidBrep{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", outer=" + outer +
                '}';
    }
}
