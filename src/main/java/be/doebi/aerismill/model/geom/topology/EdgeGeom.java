package be.doebi.aerismill.model.geom.topology;

import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.math.Point3;

public record EdgeGeom(
        String stepId,
        Curve3 curve,
        VertexGeom edgeStart,
        VertexGeom edgeEnd,
        boolean sameSense
) {
    public Point3 startPoint() {
        return edgeStart.position();
    }
    public Point3 endPoint() {
        return edgeEnd.position();
    }
}
