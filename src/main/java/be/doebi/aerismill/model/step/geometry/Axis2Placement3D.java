package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.base.ResolvableStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public class Axis2Placement3D extends ResolvableStepEntity {

    private final String name;
    private final String locationRef;
    private final String axisRef;
    private final String refDirectionRef;

    private CartesianPoint location;
    private Direction axis;
    private Direction refDirection;

    public Axis2Placement3D(String id,
                            String rawParameters,
                            String name,
                            String locationRef,
                            String axisRef,
                            String refDirectionRef) {
        super(id, StepEntityType.AXIS2_PLACEMENT_3D, rawParameters);
        this.name = name;
        this.locationRef = locationRef;
        this.axisRef = axisRef;
        this.refDirectionRef = refDirectionRef;
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

    public String getRefDirectionRef() {
        return refDirectionRef;
    }

    public CartesianPoint getLocation() {
        return location;
    }

    public Direction getAxis() {
        return axis;
    }

    public Direction getRefDirection() {
        return refDirection;
    }

    @Override
    public void doResolve(StepModel model) {
        StepEntity locationEntity = model.getEntity(locationRef);

        if (locationEntity == null) {
            throw new StepResolveException(
                    "AXIS2_PLACEMENT_3D " + getId() + " missing location reference: " + locationRef
            );
        }

        if (!(locationEntity instanceof CartesianPoint cartesianPoint)) {
            throw new StepResolveException(
                    "AXIS2_PLACEMENT_3D " + getId() + " expected CARTESIAN_POINT for location " + locationRef +
                            " but found " + locationEntity.getType()
            );
        }

        this.location = cartesianPoint;

        if (axisRef != null && !axisRef.equals("$") && !axisRef.equals("*")) {
            StepEntity axisEntity = model.getEntity(axisRef);

            if (axisEntity == null) {
                throw new StepResolveException(
                        "AXIS2_PLACEMENT_3D " + getId() + " missing axis reference: " + axisRef
                );
            }

            if (!(axisEntity instanceof Direction direction)) {
                throw new StepResolveException(
                        "AXIS2_PLACEMENT_3D " + getId() + " expected DIRECTION for axis " + axisRef +
                                " but found " + axisEntity.getType()
                );
            }

            this.axis = direction;
        }

        if (refDirectionRef != null && !refDirectionRef.equals("$") && !refDirectionRef.equals("*")) {
            StepEntity refDirectionEntity = model.getEntity(refDirectionRef);

            if (refDirectionEntity == null) {
                throw new StepResolveException(
                        "AXIS2_PLACEMENT_3D " + getId() + " missing refDirection reference: " + refDirectionRef
                );
            }

            if (!(refDirectionEntity instanceof Direction direction)) {
                throw new StepResolveException(
                        "AXIS2_PLACEMENT_3D " + getId() + " expected DIRECTION for refDirection " + refDirectionRef +
                                " but found " + refDirectionEntity.getType()
                );
            }

            this.refDirection = direction;
        }
    }

    @Override
    public String toString() {
        return "Axis2Placement3D{" +
                "id='" + getId() + '\'' +
                ", type=" + getType() +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", locationRef='" + locationRef + '\'' +
                ", axisRef='" + axisRef + '\'' +
                ", refDirectionRef='" + refDirectionRef + '\'' +
                ", location=" + location +
                ", axis=" + axis +
                ", refDirection=" + refDirection +
                '}';
    }
}