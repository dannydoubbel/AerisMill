package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;

public class Vector extends GeometricEntity {
    private final String name;
    private final Direction orientation;
    private final double magnitude;

    public Vector(String id,
                  String rawParameters,
                  String name,
                  Direction orientation,
                  double magnitude) {
        super(id, "VECTOR", rawParameters);
        this.name = name;
        this.orientation = orientation;
        this.magnitude = magnitude;
    }

    public String getName() {
        return name;
    }

    public Direction getOrientation() {
        return orientation;
    }

    public double getMagnitude() {
        return magnitude;
    }

    @Override
    public String toString() {
        return "Vector{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", orientation=" + orientation +
                ", magnitude=" + magnitude +
                '}';
    }
}
