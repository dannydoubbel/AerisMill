package be.doebi.aerismill.model.geom.surface;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

public record ToroidalSurface3(Frame3 frame, double majorRadius, double minorRadius) implements Surface3 {

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

        double ringRadius = majorRadius + minorRadius * cosV;

        Vec3 offset = x.scale(ringRadius * cosU)
                .add(y.scale(ringRadius * sinU))
                .add(z.scale(minorRadius * sinV));

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

        return x.scale(cosU * cosV)
                .add(y.scale(sinU * cosV))
                .add(z.scale(sinV));
    }
}