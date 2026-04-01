package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.math.Point3;

public final class Point3Tolerance {

    private Point3Tolerance() {
    }

    public static boolean samePoint(Point3 a, Point3 b, double eps) {
        if (a == null || b == null) {
            return false;
        }

        return Math.abs(a.x() - b.x()) <= eps
                && Math.abs(a.y() - b.y()) <= eps
                && Math.abs(a.z() - b.z()) <= eps;
    }
}