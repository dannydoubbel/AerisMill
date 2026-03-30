package be.doebi.aerismill.model.geom.curve;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

public interface Curve3 {
    Point3 pointAt(double t);
    Vec3 tangentAt(double t);
}