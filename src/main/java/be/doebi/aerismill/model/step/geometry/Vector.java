package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class Vector extends ResolvableStepEntity {
    private final String name;
    private final String orientationRef;
    private final double magnitude;

    private Direction orientation;

    public Vector(String id,
                  String rawParameters,
                  String name,
                  String orientationRef,
                  double magnitude) {
        super(id, StepEntityType.VECTOR, rawParameters);
        this.name = name;
        this.orientationRef = orientationRef;
        this.magnitude = magnitude;
    }

    public String getName() {
        return name;
    }

    public String getOrientationRef() {
        return orientationRef;
    }

    public Direction getOrientation() {
        return orientation;
    }

    public double getMagnitude() {
        return magnitude;
    }

    @Override
    public void doResolve(StepModel model) {
        StepEntity entity = model.getEntity(orientationRef);

        if (entity == null) {
            throw new StepResolveException(
                    "VECTOR " + getId() + " missing orientation reference: " + orientationRef
            );
        }

        if (!(entity instanceof Direction direction)) {
            throw new StepResolveException(
                    "VECTOR " + getId() + " expected DIRECTION for orientation " + orientationRef +
                            " but found " + entity.getType()
            );
        }

        this.orientation = direction;
    }

    @Override
    public String toString() {
        return "Vector{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", orientationRef='" + orientationRef + '\'' +
                ", orientation=" + orientation +
                ", magnitude=" + magnitude +
                '}';
    }
}