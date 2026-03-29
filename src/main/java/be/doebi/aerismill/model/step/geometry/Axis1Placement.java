package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class Axis1Placement extends ResolvableStepEntity {
    private final String name;
    private final String locationRef;
    private final String axisRef;

    private CartesianPoint location;
    private Direction axis;

    public Axis1Placement(String id,
                          String rawParameters,
                          String name,
                          String locationRef,
                          String axisRef) {
        super(id, StepEntityType.AXIS1_PLACEMENT, rawParameters);
        this.name = name;
        this.locationRef = locationRef;
        this.axisRef = axisRef;
    }

    public String getName() {
        return name;
    }

    public String getLocationRef() {
        return locationRef;
    }

    public String getAxisRef() {
        return axisRef;
    }

    public CartesianPoint getLocation() {
        return location;
    }

    public Direction getAxis() {
        return axis;
    }

    @Override
    public void doResolve(StepModel model) {
        StepEntity locationEntity = model.getEntity(locationRef);
        if (locationEntity == null) {
            throw new StepResolveException(
                    "AXIS1_PLACEMENT " + getId() + " missing location reference: " + locationRef
            );
        }

        if (!(locationEntity instanceof CartesianPoint cartesianPoint)) {
            throw new StepResolveException(
                    "AXIS1_PLACEMENT " + getId() +
                            " expected CARTESIAN_POINT for location " + locationRef +
                            " but found " + locationEntity.getType()
            );
        }

        this.location = cartesianPoint;

        if (axisRef == null) {
            this.axis = null;
            return;
        }

        StepEntity axisEntity = model.getEntity(axisRef);
        if (axisEntity == null) {
            throw new StepResolveException(
                    "AXIS1_PLACEMENT " + getId() + " missing axis reference: " + axisRef
            );
        }

        if (!(axisEntity instanceof Direction direction)) {
            throw new StepResolveException(
                    "AXIS1_PLACEMENT " + getId() +
                            " expected DIRECTION for axis " + axisRef +
                            " but found " + axisEntity.getType()
            );
        }

        this.axis = direction;
    }

    @Override
    public String toString() {
        return "Axis1Placement{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", locationRef='" + locationRef + '\'' +
                ", axisRef='" + axisRef + '\'' +
                ", location=" + location +
                ", axis=" + axis +
                '}';
    }
}