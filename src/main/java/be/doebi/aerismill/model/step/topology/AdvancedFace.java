package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.TopologyEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

import java.util.List;

public class AdvancedFace extends TopologyEntity {
    private final String name;
    private final List<StepEntity> bounds;
    private final StepEntity faceGeometry;
    private final boolean sameSense;

    public AdvancedFace(String id,
                        String rawParameters,
                        String name,
                        List<StepEntity> bounds,
                        StepEntity faceGeometry,
                        boolean sameSense) {
        super(id, StepEntityType.ADVANCED_FACE, rawParameters);
        this.name = name;
        this.bounds = bounds;
        this.faceGeometry = faceGeometry;
        this.sameSense = sameSense;
    }

    public String getName() {
        return name;
    }

    public List<StepEntity> getBounds() {
        return bounds;
    }

    public StepEntity getFaceGeometry() {
        return faceGeometry;
    }

    public boolean isSameSense() {
        return sameSense;
    }

    @Override
    public String toString() {
        return "AdvancedFace{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", bounds=" + bounds +
                ", faceGeometry=" + faceGeometry +
                ", sameSense=" + sameSense +
                '}';
    }
}
