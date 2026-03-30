package be.doebi.aerismill.model.geom.topology;

import be.doebi.aerismill.model.geom.math.Point3;

public record OrientedEdgeGeom(
        String stepId,
        EdgeGeom edge,
        boolean orientation
) {
    public Point3 start() {
        return orientation
                ? edge.edgeStart().position()
                : edge.edgeEnd().position();
    }

    public Point3 end() {
        return orientation
                ? edge.edgeEnd().position()
                : edge.edgeStart().position();
    }
}