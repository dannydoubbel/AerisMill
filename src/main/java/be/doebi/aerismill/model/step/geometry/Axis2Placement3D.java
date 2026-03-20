package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;

public class Axis2Placement3D extends GeometricEntity {
    private final String id;
    private final String name;
    private final CartesianPoint location;
    private final Direction axis;
    private final Direction refDirection;

    public Axis2Placement3D(String id,
                            String rawParameters,
                            String name,
                            CartesianPoint location,
                            Direction axis,
                            Direction refDirection) {
        super(id, rawParameters, name);
        this.id = id;
        this.name = name;
        this.location = location;
        this.axis = axis;
        this.refDirection = refDirection;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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
    public String toString() {

        return "Axis2Placement3D{" +
                "id='" + getId() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + getName() + '\'' +
                ", location=" + location +
                ", axis=" + axis +
                ", refDirection=" + refDirection +
                '}';
    }
}
