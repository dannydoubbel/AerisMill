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
        String knotSpec,
        List<Double> weights
) implements BoundedCurve3 {

    public BSplineCurve3(
            int degree,
            List<Point3> controlPoints,
            List<Double> knots,
            boolean closed,
            boolean selfIntersect,
            String curveForm,
            String knotSpec
    ) {
        this(degree, controlPoints, knots, closed, selfIntersect, curveForm, knotSpec, null);
    }

    public BSplineCurve3 {
        weights = weights == null ? null : List.copyOf(weights);
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
        if (weights != null && weights.size() != controlPoints.size()) {
            throw new IllegalArgumentException("weights size must match controlPoints size");
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

        if (weights != null) {
            return rationalPointAt(clamped, span);
        }

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

    private Point3 rationalPointAt(double t, int span) {
        double[] basis = basisFunctions(span, t);

        double weightedX = 0.0;
        double weightedY = 0.0;
        double weightedZ = 0.0;
        double weightSum = 0.0;

        for (int j = 0; j <= degree; j++) {
            int index = span - degree + j;
            double basisValue = basis[j];
            double weight = weights.get(index);
            double weightedBasis = basisValue * weight;
            Point3 point = controlPoints.get(index);

            weightedX += weightedBasis * point.x();
            weightedY += weightedBasis * point.y();
            weightedZ += weightedBasis * point.z();
            weightSum += weightedBasis;
        }

        if (weightSum == 0.0) {
            throw new IllegalStateException("Rational B-spline weight sum is zero");
        }

        return new Point3(
                weightedX / weightSum,
                weightedY / weightSum,
                weightedZ / weightSum
        );
    }

    private double[] basisFunctions(int span, double t) {
        double[] basis = new double[degree + 1];
        double[] left = new double[degree + 1];
        double[] right = new double[degree + 1];

        basis[0] = 1.0;
        for (int j = 1; j <= degree; j++) {
            left[j] = t - knots.get(span + 1 - j);
            right[j] = knots.get(span + j) - t;

            double saved = 0.0;
            for (int r = 0; r < j; r++) {
                double denom = right[r + 1] + left[j - r];
                double term = denom == 0.0 ? 0.0 : basis[r] / denom;

                basis[r] = saved + right[r + 1] * term;
                saved = left[j - r] * term;
            }
            basis[j] = saved;
        }

        return basis;
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
