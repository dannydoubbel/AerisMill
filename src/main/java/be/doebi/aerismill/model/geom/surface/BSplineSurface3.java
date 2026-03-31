package be.doebi.aerismill.model.geom.surface;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record BSplineSurface3(
        int uDegree,
        int vDegree,
        List<List<Point3>> controlPoints,
        List<Double> uKnots,
        List<Double> vKnots,
        boolean uClosed,
        boolean vClosed,
        boolean selfIntersect,
        String surfaceForm,
        String knotSpec
) implements Surface3 {

    public BSplineSurface3 {
        Objects.requireNonNull(controlPoints, "controlPoints must not be null");
        Objects.requireNonNull(uKnots, "uKnots must not be null");
        Objects.requireNonNull(vKnots, "vKnots must not be null");
        Objects.requireNonNull(surfaceForm, "surfaceForm must not be null");
        Objects.requireNonNull(knotSpec, "knotSpec must not be null");

        if (uDegree < 1) {
            throw new IllegalArgumentException("uDegree must be >= 1");
        }
        if (vDegree < 1) {
            throw new IllegalArgumentException("vDegree must be >= 1");
        }
        if (controlPoints.isEmpty()) {
            throw new IllegalArgumentException("controlPoints must not be empty");
        }

        int rowSize = controlPoints.get(0).size();
        if (rowSize == 0) {
            throw new IllegalArgumentException("control point rows must not be empty");
        }

        for (List<Point3> row : controlPoints) {
            if (row == null || row.size() != rowSize) {
                throw new IllegalArgumentException("controlPoints must form a rectangular grid");
            }
        }

        int uCount = controlPoints.size();
        int vCount = rowSize;

        if (uCount < uDegree + 1) {
            throw new IllegalArgumentException("u control point count must be >= uDegree + 1");
        }
        if (vCount < vDegree + 1) {
            throw new IllegalArgumentException("v control point count must be >= vDegree + 1");
        }

        if (uKnots.size() != uCount + uDegree + 1) {
            throw new IllegalArgumentException("Expanded uKnots size must equal uCount + uDegree + 1");
        }
        if (vKnots.size() != vCount + vDegree + 1) {
            throw new IllegalArgumentException("Expanded vKnots size must equal vCount + vDegree + 1");
        }
    }

    public double startU() {
        return uKnots.get(uDegree);
    }

    public double endU() {
        return uKnots.get(controlPoints.size());
    }

    public double startV() {
        return vKnots.get(vDegree);
    }

    public double endV() {
        return vKnots.get(controlPoints.get(0).size());
    }

    @Override
    public Point3 pointAt(double u, double v) {
        double uc = clampU(u);
        double vc = clampV(v);

        List<Point3> intermediate = new ArrayList<>(controlPoints.get(0).size());

        int vCount = controlPoints.get(0).size();
        for (int j = 0; j < vCount; j++) {
            List<Point3> columnCurve = new ArrayList<>(controlPoints.size());
            for (List<Point3> row : controlPoints) {
                columnCurve.add(row.get(j));
            }
            intermediate.add(evaluateCurvePoint(columnCurve, uDegree, uKnots, uc));
        }

        return evaluateCurvePoint(intermediate, vDegree, vKnots, vc);
    }

    @Override
    public Vec3 normalAt(double u, double v) {
        double du = Math.max(1e-6, (endU() - startU()) * 1e-6);
        double dv = Math.max(1e-6, (endV() - startV()) * 1e-6);

        double u0 = Math.max(startU(), u - du);
        double u1 = Math.min(endU(), u + du);
        double v0 = Math.max(startV(), v - dv);
        double v1 = Math.min(endV(), v + dv);

        Point3 pu0 = pointAt(u0, v);
        Point3 pu1 = pointAt(u1, v);
        Point3 pv0 = pointAt(u, v0);
        Point3 pv1 = pointAt(u, v1);

        Vec3 tu = pu1.subtract(pu0);
        Vec3 tv = pv1.subtract(pv0);

        return tu.cross(tv);
    }

    private double clampU(double u) {
        double start = startU();
        double end = endU();

        if (u < start) return start;
        if (u > end) return end;
        if (u == end) return Math.nextDown(end);
        return u;
    }

    private double clampV(double v) {
        double start = startV();
        double end = endV();

        if (v < start) return start;
        if (v > end) return end;
        if (v == end) return Math.nextDown(end);
        return v;
    }

    private static Point3 evaluateCurvePoint(List<Point3> controlPoints, int degree, List<Double> knots, double t) {
        int span = findSpan(controlPoints.size(), degree, knots, t);

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
                    alpha = (t - left) / denom;
                }

                d[j] = lerp(d[j - 1], d[j], alpha);
            }
        }

        return d[degree];
    }

    private static int findSpan(int controlPointCount, int degree, List<Double> knots, double t) {
        int n = controlPointCount - 1;

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