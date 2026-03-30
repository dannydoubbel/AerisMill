package be.doebi.aerismill.model.geom.topology;

import be.doebi.aerismill.model.geom.math.Point3;

public record VertexGeom(
        String stepId,
        Point3 position
) {}