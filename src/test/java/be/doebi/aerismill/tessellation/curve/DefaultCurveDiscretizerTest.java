package be.doebi.aerismill.tessellation.curve;

import be.doebi.aerismill.model.geom.curve.BSplineCurve3;
import be.doebi.aerismill.model.geom.curve.CircleCurve3;
import be.doebi.aerismill.model.geom.curve.EllipseCurve3;
import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.VertexGeom;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCurveDiscretizerTest {

    @Test
    void discretize_lineCurve_returnsStartAndEndPoints() {
        DefaultCurveDiscretizer discretizer = new DefaultCurveDiscretizer();

        Point3 start = new Point3(1.0, 2.0, 3.0);
        Point3 end = new Point3(4.0, 5.0, 6.0);

        EdgeGeom edge = edge(
                "#edge1",
                new LineCurve3(start, unit(1.0, 0.0, 0.0)),
                start,
                end,
                true
        );

        List<Point3> result = discretizer.discretize(edge);

        assertEquals(List.of(start, end), result);
    }

    @Test
    void discretize_closedCircle_returnsMoreThanTwoPoints() {
        DefaultCurveDiscretizer discretizer = new DefaultCurveDiscretizer();

        Frame3 frame = frame(
                new Point3(0.0, 0.0, 0.0),
                unit(1.0, 0.0, 0.0),
                unit(0.0, 1.0, 0.0),
                unit(0.0, 0.0, 1.0)
        );

        CircleCurve3 circle = new CircleCurve3(frame, 10.0);

        Point3 seam = circle.pointAt(0.0);

        EdgeGeom edge = edge(
                "#edge2",
                circle,
                seam,
                seam,
                true
        );

        List<Point3> result = discretizer.discretize(edge);

        assertTrue(result.size() > 2);
        assertEquals(seam, result.getFirst());
        assertEquals(seam, result.getLast());
    }

    @Test
    void discretize_closedEllipse_returnsMoreThanTwoPoints() {
        DefaultCurveDiscretizer discretizer = new DefaultCurveDiscretizer();

        Frame3 frame = frame(
                new Point3(0.0, 0.0, 0.0),
                unit(1.0, 0.0, 0.0),
                unit(0.0, 1.0, 0.0),
                unit(0.0, 0.0, 1.0)
        );

        EllipseCurve3 ellipse = new EllipseCurve3(frame, 8.0, 4.0);

        Point3 seam = ellipse.pointAt(0.0);

        EdgeGeom edge = edge(
                "#edge3",
                ellipse,
                seam,
                seam,
                true
        );

        List<Point3> result = discretizer.discretize(edge);

        assertTrue(result.size() > 2);
        assertEquals(seam, result.getFirst());
        assertEquals(seam, result.getLast());
    }

    @Test
    void discretize_boundedSpline_returnsMoreThanTwoPoints() {
        DefaultCurveDiscretizer discretizer = new DefaultCurveDiscretizer();

        Point3 p0 = new Point3(0.0, 0.0, 0.0);
        Point3 p1 = new Point3(1.0, 2.0, 0.0);
        Point3 p2 = new Point3(3.0, 2.0, 0.0);
        Point3 p3 = new Point3(4.0, 0.0, 0.0);

        BSplineCurve3 spline = new BSplineCurve3(
                2,
                List.of(p0, p1, p2, p3),
                List.of(0.0, 0.0, 0.0, 1.0, 2.0, 2.0, 2.0),
                false,
                false,
                "UNSPECIFIED",
                "UNSPECIFIED"
        );

        Point3 start = spline.pointAt(spline.startParam());
        Point3 end = spline.pointAt(spline.endParam());

        EdgeGeom edge = edge(
                "#edge4",
                spline,
                start,
                end,
                true
        );

        List<Point3> result = discretizer.discretize(edge);

        assertTrue(result.size() > 2);
        assertEquals(start, result.getFirst());
        assertEquals(end, result.getLast());
    }

    @Test
    void discretize_circleArc_startsAtExactTopologicalStartPoint() {
        DefaultCurveDiscretizer discretizer = new DefaultCurveDiscretizer();

        Frame3 frame = frame(
                new Point3(0.0, 0.0, 0.0),
                unit(1.0, 0.0, 0.0),
                unit(0.0, 1.0, 0.0),
                unit(0.0, 0.0, 1.0)
        );

        CircleCurve3 circle = new CircleCurve3(frame, 5.0);

        Point3 start = circle.pointAt(0.0);
        Point3 end = circle.pointAt(Math.PI / 2.0);

        EdgeGeom edge = edge(
                "#edge5",
                circle,
                start,
                end,
                true
        );

        List<Point3> result = discretizer.discretize(edge);

        assertEquals(start, result.getFirst());
    }

    @Test
    void discretize_circleArc_endsAtExactTopologicalEndPoint() {
        DefaultCurveDiscretizer discretizer = new DefaultCurveDiscretizer();

        Frame3 frame = frame(
                new Point3(0.0, 0.0, 0.0),
                unit(1.0, 0.0, 0.0),
                unit(0.0, 1.0, 0.0),
                unit(0.0, 0.0, 1.0)
        );

        CircleCurve3 circle = new CircleCurve3(frame, 5.0);

        Point3 start = circle.pointAt(0.0);
        Point3 end = circle.pointAt(Math.PI / 2.0);

        EdgeGeom edge = edge(
                "#edge6",
                circle,
                start,
                end,
                true
        );

        List<Point3> result = discretizer.discretize(edge);

        assertEquals(end, result.getLast());
    }

    private EdgeGeom edge(
            String stepId,
            be.doebi.aerismill.model.geom.curve.Curve3 curve,
            Point3 start,
            Point3 end,
            boolean sameSense
    ) {
        return new EdgeGeom(
                stepId,
                curve,
                new VertexGeom(stepId + "_start", start),
                new VertexGeom(stepId + "_end", end),
                sameSense
        );
    }

    private Frame3 frame(Point3 origin, UnitVec3 xAxis, UnitVec3 yAxis, UnitVec3 zAxis) {
        return new Frame3(origin, xAxis, yAxis, zAxis);
    }

    private UnitVec3 unit(double x, double y, double z) {
        return UnitVec3.of(new Vec3(x, y, z));
    }
}