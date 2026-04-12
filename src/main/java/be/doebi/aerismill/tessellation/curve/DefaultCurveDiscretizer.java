package be.doebi.aerismill.tessellation.curve;

import be.doebi.aerismill.model.geom.curve.BSplineCurve3;
import be.doebi.aerismill.model.geom.curve.BoundedCurve3;
import be.doebi.aerismill.model.geom.curve.CircleCurve3;
import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.curve.EllipseCurve3;
import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DefaultCurveDiscretizer implements CurveDiscretizer {

    private static final int DEFAULT_CIRCLE_SEGMENTS = 24;
    private static final int DEFAULT_ELLIPSE_SEGMENTS = 24;
    private static final int DEFAULT_BOUNDED_SEGMENTS = 16;

    @Override
    public List<Point3> discretize(EdgeGeom edge) {
        Objects.requireNonNull(edge, "edge must not be null");
        Objects.requireNonNull(edge.curve(), "edge curve must not be null");
        Objects.requireNonNull(edge.edgeStart(), "edge start vertex must not be null");
        Objects.requireNonNull(edge.edgeEnd(), "edge end vertex must not be null");

        Curve3 curve = edge.curve();

        if (curve instanceof LineCurve3) {
            return discretizeLine(edge);
        }
        if (curve instanceof CircleCurve3 circle) {
            return discretizeCircle(edge, circle);
        }
        if (curve instanceof EllipseCurve3 ellipse) {
            return discretizeEllipse(edge, ellipse);
        }
        if (curve instanceof BSplineCurve3 spline) {
            return discretizeBounded(edge, spline, DEFAULT_BOUNDED_SEGMENTS);
        }
        if (curve instanceof BoundedCurve3 bounded) {
            return discretizeBounded(edge, bounded, DEFAULT_BOUNDED_SEGMENTS);
        }

        return List.of(edge.startPoint(), edge.endPoint());
    }

    private List<Point3> discretizeLine(EdgeGeom edge) {
        return List.of(edge.startPoint(), edge.endPoint());
    }

    private List<Point3> discretizeCircle(EdgeGeom edge, CircleCurve3 circle) {
        double startAngle = angleOnFrame(circle.frame(), edge.startPoint(), 1.0, 1.0);
        double endAngle = angleOnFrame(circle.frame(), edge.endPoint(), 1.0, 1.0);

        return samplePeriodicCurve(
                edge,
                circle,
                startAngle,
                endAngle,
                Math.PI * 2.0,
                DEFAULT_CIRCLE_SEGMENTS
        );
    }

    private List<Point3> discretizeEllipse(EdgeGeom edge, EllipseCurve3 ellipse) {
        double startAngle = angleOnFrame(
                ellipse.frame(),
                edge.startPoint(),
                ellipse.semiAxis1(),
                ellipse.semiAxis2()
        );
        double endAngle = angleOnFrame(
                ellipse.frame(),
                edge.endPoint(),
                ellipse.semiAxis1(),
                ellipse.semiAxis2()
        );

        return samplePeriodicCurve(
                edge,
                ellipse,
                startAngle,
                endAngle,
                Math.PI * 2.0,
                DEFAULT_ELLIPSE_SEGMENTS
        );
    }

    private List<Point3> discretizeBounded(EdgeGeom edge, BoundedCurve3 curve, int segments) {
        double start = curve.startParam();
        double end = curve.endParam();

        if (!edge.sameSense()) {
            double tmp = start;
            start = end;
            end = tmp;
        }

        return sampleCurveRange(edge, curve, start, end, segments);
    }

    private List<Point3> samplePeriodicCurve(
            EdgeGeom edge,
            Curve3 curve,
            double startParam,
            double endParam,
            double period,
            int segments
    ) {
        double start = normalizeAngle(startParam, period);
        double end = normalizeAngle(endParam, period);

        boolean closedEdge = pointsEqual(edge.startPoint(), edge.endPoint());

        if (closedEdge) {
            end = edge.sameSense() ? start + period : start - period;
        } else if (edge.sameSense()) {
            while (end <= start) {
                end += period;
            }
        } else {
            while (end >= start) {
                end -= period;
            }
        }

        return sampleCurveRange(edge, curve, start, end, segments);
    }

    private List<Point3> sampleCurveRange(
            EdgeGeom edge,
            Curve3 curve,
            double startParam,
            double endParam,
            int segments
    ) {
        int safeSegments = Math.max(1, segments);
        List<Point3> points = new ArrayList<>(safeSegments + 1);

        for (int i = 0; i <= safeSegments; i++) {
            double alpha = i / (double) safeSegments;
            double t = startParam + (endParam - startParam) * alpha;
            points.add(curve.pointAt(t));
        }

        // Snap endpoints to exact topological vertices.
        points.set(0, edge.startPoint());
        points.set(points.size() - 1, edge.endPoint());

        return points;
    }

    private double angleOnFrame(Frame3 frame, Point3 point, double scaleX, double scaleY) {
        Point3 origin = frame.origin();
        Vec3 delta = point.subtract(origin);

        double x = delta.dot(frame.xAxis().toVec3());
        double y = delta.dot(frame.yAxis().toVec3());

        double normalizedX = scaleX == 0.0 ? x : x / scaleX;
        double normalizedY = scaleY == 0.0 ? y : y / scaleY;

        return Math.atan2(normalizedY, normalizedX);
    }

    private double normalizeAngle(double angle, double period) {
        double normalized = angle % period;
        return normalized < 0.0 ? normalized + period : normalized;
    }

    private boolean pointsEqual(Point3 a, Point3 b) {
        return a.equals(b);
    }
}