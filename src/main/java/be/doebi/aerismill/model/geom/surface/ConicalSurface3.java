package be.doebi.aerismill.model.geom.surface;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

public record ConicalSurface3(Frame3 frame, double radius, double semiAngle) implements Surface3 {

    @Override
    public Point3 pointAt(double u, double v) {
        double cos = Math.cos(u);
        double sin = Math.sin(u);

        double localRadius = radius + v * Math.tan(semiAngle);

        Point3 origin = frame.origin();
        Vec3 x = frame.xAxis().toVec3();
        Vec3 y = frame.yAxis().toVec3();
        Vec3 z = frame.zAxis().toVec3();

        Vec3 radial = x.scale(localRadius * cos).add(y.scale(localRadius * sin));
        Vec3 axial = z.scale(v);

        return origin.add(radial).add(axial);
    }

    @Override
    public Vec3 normalAt(double u, double v) {
        double cos = Math.cos(u);
        double sin = Math.sin(u);

        double slope = Math.tan(semiAngle);

        Vec3 x = frame.xAxis().toVec3();
        Vec3 y = frame.yAxis().toVec3();
        Vec3 z = frame.zAxis().toVec3();

        Vec3 radialUnit = x.scale(cos).add(y.scale(sin));

        return radialUnit.add(z.scale(-slope));
    }
}