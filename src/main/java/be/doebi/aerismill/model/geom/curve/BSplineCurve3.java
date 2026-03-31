package be.doebi.aerismill.model.geom.curve;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

import java.util.List;
import java.util.Objects;

public record BSplineCurve3(
        int degree,
        List<Point3> controlPoints,
        List<Double> knots,
        boolean closed,
        boolean selfIntersect,
        String curveForm,
        String knotSpec
) implements BoundedCurve3 {

    public BSplineCurve3 {
        Objects.requireNonNull(controlPoints, "controlPoints must not be null");
        Objects.requireNonNull(knots, "knots must not be null");
        Objects.requireNonNull(curveForm, "curveForm must not be null");
        Objects.requireNonNull(knotSpec, "knotSpec must not be null");

        if (degree < 1) {
            throw new IllegalArgumentException("degree must be >= 1");
        }
        if (controlPoints.size() < degree + 1) {
            throw new IllegalArgumentException("controlPoints size must be >= degree + 1");
        }
        if (knots.size() != controlPoints.size() + degree + 1) {
            throw new IllegalArgumentException(
                    "Expanded knot vector size must equal controlPoints.size() + degree + 1"
            );
        }
    }

    public double startParam() {
        return knots.get(degree);
    }

    public double endParam() {
        return knots.get(controlPoints.size());
    }

    @Override
    public Point3 pointAt(double t) {
        double clamped = clampToDomain(t);
        int span = findSpan(clamped);

        Point3[] d = new Point3[degree + 1];
        for (int j = 0; j <= degree; j++) {
            d[j] = controlPoints.get(span - degree + j);
        }

        for (int r = 1; r <= degree; r++) {
            for (int j = degree; j >= r; j--) {
                int i = span - degree + j;
                double left = knots.get(i);
                double right = knots.get(i + degree - r + 1);

                double alpha;
                double denom = right - left;
                if (denom == 0.0) {
                    alpha = 0.0;
                } else {
                    alpha = (clamped - left) / denom;
                }

                d[j] = lerp(d[j - 1], d[j], alpha);
            }
        }

        return d[degree];
    }

    @Override
    public Vec3 tangentAt(double t) {
        double domainStart = startParam();
        double domainEnd = endParam();
        double eps = Math.max(1e-6, (domainEnd - domainStart) * 1e-6);

        double t0 = Math.max(domainStart, t - eps);
        double t1 = Math.min(domainEnd, t + eps);

        if (t1 == t0) {
            return new Vec3(0.0, 0.0, 0.0);
        }

        Point3 p0 = pointAt(t0);
        Point3 p1 = pointAt(t1);
        return p1.subtract(p0).scale(1.0 / (t1 - t0));
    }

    private double clampToDomain(double t) {
        double start = startParam();
        double end = endParam();

        if (t < start) {
            return start;
        }
        if (t > end) {
            return end;
        }

        if (t == end) {
            return Math.nextDown(end);
        }

        return t;
    }

    private int findSpan(double t) {
        int n = controlPoints.size() - 1;

        if (t >= knots.get(n + 1)) {
            return n;
        }
        if (t <= knots.get(degree)) {
            return degree;
        }

        int low = degree;
        int high = n + 1;
        int mid = (low + high) / 2;

        while (t < knots.get(mid) || t >= knots.get(mid + 1)) {
            if (t < knots.get(mid)) {
                high = mid;
            } else {
                low = mid;
            }
            mid = (low + high) / 2;
        }

        return mid;
    }

    private static Point3 lerp(Point3 a, Point3 b, double alpha) {
        double beta = 1.0 - alpha;
        return new Point3(
                beta * a.x() + alpha * b.x(),
                beta * a.y() + alpha * b.y(),
                beta * a.z() + alpha * b.z()
        );
    }
}