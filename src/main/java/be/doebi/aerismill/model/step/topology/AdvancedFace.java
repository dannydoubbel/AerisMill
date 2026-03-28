package be.doebi.aerismill.model.step.topology;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

import java.util.ArrayList;
import java.util.List;

public class AdvancedFace extends ResolvableStepEntity {
    private final String name;
    private final List<String> boundRefs;
    private final String faceGeometryRef;
    private final boolean sameSense;

    private final List<StepEntity> bounds;
    private StepEntity faceGeometry;

    public AdvancedFace(String id,
                        String rawParameters,
                        String name,
                        List<String> boundRefs,
                        String faceGeometryRef,
                        boolean sameSense) {
        super(id, StepEntityType.ADVANCED_FACE, rawParameters);
        this.name = name;
        this.boundRefs = boundRefs;
        this.faceGeometryRef = faceGeometryRef;
        this.sameSense = sameSense;
        this.bounds = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getBoundRefs() {
        return boundRefs;
    }

    public String getFaceGeometryRef() {
        return faceGeometryRef;
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
    public void doResolve(StepModel model) {
        bounds.clear();

        for (String boundRef : boundRefs) {
            StepEntity entity = model.getEntity(boundRef);

            if (entity == null) {
                throw new StepResolveException(
                        "ADVANCED_FACE " + getId() + " missing bound reference: " + boundRef
                );
            }

            bounds.add(entity);
        }

        StepEntity geometryEntity = model.getEntity(faceGeometryRef);

        if (geometryEntity == null) {
            throw new StepResolveException(
                    "ADVANCED_FACE " + getId() + " missing face geometry reference: " + faceGeometryRef
            );
        }

        this.faceGeometry = geometryEntity;
    }

    @Override
    public String toString() {
        return "AdvancedFace{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", boundRefs=" + boundRefs +
                ", bounds=" + bounds +
                ", faceGeometryRef='" + faceGeometryRef + '\'' +
                ", faceGeometry=" + faceGeometry +
                ", sameSense=" + sameSense +
                '}';
    }
}