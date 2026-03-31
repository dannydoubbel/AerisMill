package be.doebi.aerismill.model.geom.surface;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

public record SphericalSurface3(Frame3 frame, double radius) implements Surface3 {

    @Override
    public Point3 pointAt(double u, double v) {
        double cosU = Math.cos(u);
        double sinU = Math.sin(u);
        double cosV = Math.cos(v);
        double sinV = Math.sin(v);

        Point3 origin = frame.origin();
        Vec3 x = frame.xAxis().toVec3();
        Vec3 y = frame.yAxis().toVec3();
        Vec3 z = frame.zAxis().toVec3();

        Vec3 offset = x.scale(radius * cosV * cosU)
                .add(y.scale(radius * cosV * sinU))
                .add(z.scale(radius * sinV));

        return origin.add(offset);
    }

    @Override
    public Vec3 normalAt(double u, double v) {
        double cosU = Math.cos(u);
        double sinU = Math.sin(u);
        double cosV = Math.cos(v);
        double sinV = Math.sin(v);

        Vec3 x = frame.xAxis().toVec3();
        Vec3 y = frame.yAxis().toVec3();
        Vec3 z = frame.zAxis().toVec3();

        return x.scale(cosV * cosU)
                .add(y.scale(cosV * sinU))
                .add(z.scale(sinV));
    }
}