package be.doebi.aerismill.model.geom.surface;

import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;

import java.util.Objects;

public record SurfaceOfRevolution3(
        Curve3 sweptCurve,
        Point3 axisOrigin,
        UnitVec3 axisDirection
) implements Surface3 {

    public SurfaceOfRevolution3 {
        Objects.requireNonNull(sweptCurve, "sweptCurve must not be null");
        Objects.requireNonNull(axisOrigin, "axisOrigin must not be null");
        Objects.requireNonNull(axisDirection, "axisDirection must not be null");
    }

    @Override
    public Point3 pointAt(double u, double v) {
        Point3 basePoint = sweptCurve.pointAt(v);
        return rotatePointAroundAxis(basePoint, u);
    }

    @Override
    public Vec3 normalAt(double u, double v) {
        double du = 1e-6;
        double dv = 1e-6;

        Point3 pu0 = pointAt(u - du, v);
        Point3 pu1 = pointAt(u + du, v);
        Point3 pv0 = pointAt(u, v - dv);
        Point3 pv1 = pointAt(u, v + dv);

        Vec3 tangentU = pu1.subtract(pu0);
        Vec3 tangentV = pv1.subtract(pv0);

        return tangentU.cross(tangentV);
    }

    private Point3 rotatePointAroundAxis(Point3 point, double angle) {
        Vec3 k = axisDirection.toVec3();
        Vec3 relative = point.subtract(axisOrigin);

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        Vec3 term1 = relative.scale(cos);
        Vec3 term2 = k.cross(relative).scale(sin);
        Vec3 term3 = k.scale(k.dot(relative) * (1.0 - cos));

        return axisOrigin.add(term1.add(term2).add(term3));
    }
}