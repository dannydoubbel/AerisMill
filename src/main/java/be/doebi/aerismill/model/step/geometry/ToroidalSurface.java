package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;

public class ToroidalSurface extends GeometricEntity {
    private final String name;
    private final Axis2Placement3D position;
    private final double majorRadius;
    private final double minorRadius;

    public ToroidalSurface(String id,
                           String rawParameters,
                           String name,
                           Axis2Placement3D position,
                           double majorRadius,
                           double minorRadius) {
        super(id, "TOROIDAL_SURFACE", rawParameters);
        this.name = name;
        this.position = position;
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
    }

    public String getName() {
        return name;
    }

    public Axis2Placement3D getPosition() {
        return position;
    }

    public double getMajorRadius() {
        return majorRadius;
    }

    public double getMinorRadius() {
        return minorRadius;
    }

    @Override
    public String toString() {
        return "ToroidalSurface{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", majorRadius=" + majorRadius +
                ", minorRadius=" + minorRadius +
                '}';
    }
}
