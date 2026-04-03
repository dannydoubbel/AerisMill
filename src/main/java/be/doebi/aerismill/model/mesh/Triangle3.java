package be.doebi.aerismill.model.mesh;

import be.doebi.aerismill.model.geom.math.Point3;

public record Triangle3(
        Point3 a,
        Point3 b,
        Point3 c
) {}