package be.doebi.aerismill.tessellation.polygon;

import java.util.List;

public final class PolygonMath {

    private PolygonMath() {
    }

    public static double signedArea(List<Point2> points) {
        if (points == null || points.size() < 3) {
            return 0.0;
        }

        double area = 0.0;
        for (int i = 0; i < points.size(); i++) {
            Point2 a = points.get(i);
            Point2 b = points.get((i + 1) % points.size());
            area += a.x() * b.y() - b.x() * a.y();
        }
        return 0.5 * area;
    }

    public static double twiceSignedArea(Point2 a, Point2 b, Point2 c) {
        return (b.x() - a.x()) * (c.y() - a.y()) -
                (b.y() - a.y()) * (c.x() - a.x());
    }
}