package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;

public class Plane extends GeometricEntity {
    private final String name;
    private final Axis2Placement3D position;

    public Plane(String id,
                 String rawParameters,
                 String name,
                 Axis2Placement3D position) {
        super(id, "PLANE", rawParameters);
        this.name = name;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public Axis2Placement3D getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "Plane{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", position=" + position +
                '}';
    }
}
