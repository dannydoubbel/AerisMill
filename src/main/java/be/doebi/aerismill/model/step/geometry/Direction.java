package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;

import java.util.List;

public class Direction extends GeometricEntity {
    private final String name;
    private final List<Double> directionRatios;


    public Direction(String id, String rawParameters, String name, List<Double> directionRatios) {
        super(id, "DIRECTION", rawParameters);
        this.name = name;
        this.directionRatios = directionRatios;
    }

    public String getName() {
        return name;
    }

    public List<Double> getDirectionRatios() {
        return directionRatios;
    }

    public Double getX() {
        return directionRatios.size() > 0 ? directionRatios.get(0) : null;
    }

    public Double getY() {
        return directionRatios.size() > 1 ? directionRatios.get(1) : null;
    }

    public Double getZ() {
        return directionRatios.size() > 2 ? directionRatios.get(2) : null;
    }

    @Override
    public String toString() {
        return "Direction{id='" + getId() + "', name='" + name + "', directionRatios=" + directionRatios + "}";
    }
}
