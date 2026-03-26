package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

public class CylindricalSurface extends GeometricEntity {
    private final String name;
    private final Axis2Placement3D position;
    private final double radius;

    public CylindricalSurface(String id,
                              String rawParameters,
                              String name,
                              Axis2Placement3D position,
                              double radius) {
        super(id, StepEntityType.CYLINDRICAL_SURFACE, rawParameters);
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
        return "CylindricalSurface{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                ", radius=" + radius +
                '}';
    }
}
