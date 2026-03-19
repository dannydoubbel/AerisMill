package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;

import java.util.List;

public class CartesianPoint extends GeometricEntity {
    private final String name;
    private final List<Double> coordinates;

    public CartesianPoint(String id, String rawParameters, String name, List<Double> coordinates) {
        super(id, "CARTESIAN_POINT", rawParameters);
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public Double getX() {
        return coordinates.size() > 0 ? coordinates.get(0) : null;
    }

    public Double getY() {
        return coordinates.size() > 1 ? coordinates.get(1) : null;
    }

    public Double getZ() {
        return coordinates.size() > 2 ? coordinates.get(2) : null;
    }

    @Override
    public String toString() {
        return "CartesianPoint{id='" + getId() + "', name='" + name + "', coordinates=" + coordinates + "}";
    }
}
