package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

public class Circle extends GeometricEntity {
    private final String name;
    private final Axis2Placement3D position;
    private final double radius;

    public Circle(String id,
                  String rawParameters,
                  String name,
                  Axis2Placement3D position,
                  double radius) {
        super(id, StepEntityType.CIRCLE, rawParameters);
        this.name = name;
        this.position = position;
        this.radius = radius;
    }

    public String getName() {
        return name;
    }

    public Axis2Placement3D getPosition() {
        return position;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", radius=" + radius +
                '}';
    }
}
