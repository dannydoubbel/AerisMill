package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

import java.util.List;

public class ClosedShell extends TopologyEntity {
    private final String name;
    private final List<String> faceRefs;

    private List<AdvancedFace> faces;

    public ClosedShell(String id,
                       String rawParameters,
                       String name,
                       List<String> faceRefs) {
        super(id, StepEntityType.CLOSED_SHELL, rawParameters);
        this.name = name;
        this.faceRefs = faceRefs;
    }

    public String getName() {
        return name;
    }

    public List<String> getFaceRefs() {
        return faceRefs;
    }

    public List<AdvancedFace> getFaces() {
        return faces;
    }

    @Override
    protected void doResolve(StepModel model) {
        this.faces = model.resolveEntityList(faceRefs, AdvancedFace.class);
    }

    @Override
    public String toString() {
        return "ClosedShell{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", faceRefs=" + faceRefs +
                ", faces=" + faces +
                '}';
    }
}