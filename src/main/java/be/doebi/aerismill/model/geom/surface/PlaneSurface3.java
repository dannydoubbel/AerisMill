package be.doebi.aerismill.model.geom.surface;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

public record PlaneSurface3(Frame3 frame) implements Surface3 {

    @Override
    public Point3 pointAt(double u, double v) {
        Point3 origin = frame.origin();
        Vec3 x = frame.xAxis().toVec3();
        Vec3 y = frame.yAxis().toVec3();

        return origin.add(x.scale(u)).add(y.scale(v));
    }

    @Override
    public Vec3 normalAt(double u, double v) {
        return frame.zAxis().toVec3();
    }
}