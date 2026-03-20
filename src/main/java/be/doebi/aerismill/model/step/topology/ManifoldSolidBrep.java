package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;

public class ManifoldSolidBrep extends TopologyEntity {
    private final String name;
    private final ClosedShell outer;

    public ManifoldSolidBrep(String id,
                             String rawParameters,
                             String name,
                             ClosedShell outer) {
        super(id, "MANIFOLD_SOLID_BREP", rawParameters);
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
