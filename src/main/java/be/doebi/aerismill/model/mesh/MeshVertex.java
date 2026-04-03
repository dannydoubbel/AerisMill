package be.doebi.aerismill.model.mesh;

import be.doebi.aerismill.model.geom.math.Point3;

public record MeshVertex(
        int index,
        Point3 point
) {}