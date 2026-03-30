package be.doebi.aerismill.model.geom.curve;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;

public record LineCurve3(Point3 origin, UnitVec3 direction) implements Curve3 {

    @Override
    public Point3 pointAt(double t) {
        return origin.add(direction.toVec3().scale(t));
    }

    @Override
    public Vec3 tangentAt(double t) {
        return direction.toVec3();
    }
}