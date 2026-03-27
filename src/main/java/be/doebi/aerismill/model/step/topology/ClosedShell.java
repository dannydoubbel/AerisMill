package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

import java.util.List;

public class ClosedShell extends TopologyEntity {
    private final String name;
    private List<AdvancedFace> faceRefs;

    public ClosedShell(String id,
                       String rawParameters,
                       String name,
                       List<AdvancedFace> faceRefs) {
        super(id, StepEntityType.CLOSED_SHELL, rawParameters);
        this.name = name;
        this.faceRefs = faceRefs;
    }

    public String getName() {
        return name;
    }

    public List<AdvancedFace> getCfsFaces() {
        return cfsFaces;
    }

    @Override
    protected void doResolve(StepModel model) {
        this.faceRefs = model.resolveEntityList(faceRefs, AdvancedFace.class);
    }

    @Override
    public String toString() {
        return "ClosedShell{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", cfsFaces=" + cfsFaces +
                '}';
    }
}
