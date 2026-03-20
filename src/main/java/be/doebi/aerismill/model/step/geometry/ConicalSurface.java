package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;

public class ConicalSurface extends GeometricEntity {
    private final String name;
    private final Axis2Placement3D position;
    private final double radius;
    private final double semiAngle;

    public ConicalSurface(String id,
                          String rawParameters,
                          String name,
                          Axis2Placement3D position,
                          double radius,
                          double semiAngle) {
        super(id, "CONICAL_SURFACE", rawParameters);
        this.name = name;
        this.position = position;
        this.radius = radius;
        this.semiAngle = semiAngle;
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

    public double getSemiAngle() {
        return semiAngle;
    }

    @Override
    public String toString() {
        return "ConicalSurface{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", radius=" + radius +
                ", semiAngle=" + semiAngle +
                '}';
    }
}
