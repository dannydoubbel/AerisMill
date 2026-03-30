package be.doebi.aerismill.model.geom.surface;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

public interface Surface3 {
    Point3 pointAt(double u, double v);
    Vec3 normalAt(double u, double v);
}