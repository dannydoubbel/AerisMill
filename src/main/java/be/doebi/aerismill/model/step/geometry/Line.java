package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

public class Line extends GeometricEntity {
    private final String name;
    private final CartesianPoint point;
    private final Vector vector;

    public Line(String id,
                String rawParameters,
                String name,
                CartesianPoint point,
                Vector vector) {
        super(id, StepEntityType.LINE, rawParameters);
        this.name = name;
        this.point = point;
        this.vector = vector;
    }

    public String getName() {
        return name;
    }

    public CartesianPoint getPoint() {
        return point;
    }

    public Vector getVector() {
        return vector;
    }

    @Override
    public String toString() {
        return "Line{" +
                "id='" + getId() + '\'' +
                ", type='" + getType() + '\'' +
                ", rawParameters='" + getRawParameters() + '\'' +
                ", name='" + name + '\'' +
                ", point=" + point +
                ", vector=" + vector +
                '}';
    }
}
