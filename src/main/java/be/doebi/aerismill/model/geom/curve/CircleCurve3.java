package be.doebi.aerismill.model.geom.curve;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

public record CircleCurve3(Frame3 frame, double radius) implements Curve3 {

    @Override
    public Point3 pointAt(double t) {
        double cos = Math.cos(t);
        double sin = Math.sin(t);

        Point3 origin = frame.origin();
        Vec3 x = frame.xAxis().toVec3();
        Vec3 y = frame.yAxis().toVec3();

        Vec3 offset = x.scale(radius * cos).add(y.scale(radius * sin));
        return origin.add(offset);
    }

    @Override
    public Vec3 tangentAt(double t) {
        double cos = Math.cos(t);
        double sin = Math.sin(t);

        Vec3 x = frame.xAxis().toVec3();
        Vec3 y = frame.yAxis().toVec3();

        return x.scale(-radius * sin).add(y.scale(radius * cos));
    }
}
