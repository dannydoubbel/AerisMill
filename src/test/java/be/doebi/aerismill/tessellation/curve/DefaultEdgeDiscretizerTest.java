package be.doebi.aerismill.tessellation.curve;

import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.model.geom.topology.VertexGeom;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultEdgeDiscretizerTest {

    @Test
    void discretize_returnsOrientedStartAndEnd() {
        Point3 p1 = new Point3(1.0, 2.0, 3.0);
        Point3 p2 = new Point3(4.0, 5.0, 6.0);

        VertexGeom v1 = new VertexGeom("#v1", p1);
        VertexGeom v2 = new VertexGeom("#v2", p2);

        EdgeGeom edge = new EdgeGeom(
                "#e1",
                new LineCurve3(p1, UnitVec3.of(p2.subtract(p1))),
                v1,
                v2,
                true
        );

        OrientedEdgeGeom orientedEdge = new OrientedEdgeGeom(
                "#oe1",
                edge,
                true
        );

        DefaultEdgeDiscretizer discretizer = new DefaultEdgeDiscretizer();

        List<Point3> result = discretizer.discretize(
                orientedEdge,
                GeometryTolerance.defaults()
        );

        assertEquals(List.of(p1, p2), result);
    }

    @Test
    void discretize_reversedEdge_returnsReversedOrder() {
        Point3 p1 = new Point3(1.0, 2.0, 3.0);
        Point3 p2 = new Point3(4.0, 5.0, 6.0);

        VertexGeom v1 = new VertexGeom("#v1", p1);
        VertexGeom v2 = new VertexGeom("#v2", p2);

        EdgeGeom edge = new EdgeGeom(
                "#e1",
                new LineCurve3(p1, UnitVec3.of(p2.subtract(p1))),
                v1,
                v2,
                true
        );

        OrientedEdgeGeom orientedEdge = new OrientedEdgeGeom(
                "#oe1",
                edge,
                false
        );

        DefaultEdgeDiscretizer discretizer = new DefaultEdgeDiscretizer();

        List<Point3> result = discretizer.discretize(
                orientedEdge,
                GeometryTolerance.defaults()
        );

        assertEquals(List.of(p2, p1), result);
    }

    @Test
    void discretize_nullEdge_throwsIllegalArgumentException() {
        DefaultEdgeDiscretizer discretizer = new DefaultEdgeDiscretizer();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> discretizer.discretize(null, GeometryTolerance.defaults())
        );

        assertEquals("Edge must not be null.", exception.getMessage());
    }


    @Test
    void discretize_delegatesToCurveDiscretizer() {
        RecordingCurveDiscretizer curveDiscretizer = new RecordingCurveDiscretizer();
        DefaultEdgeDiscretizer edgeDiscretizer = new DefaultEdgeDiscretizer(curveDiscretizer);

        EdgeGeom edge = edgeGeom(
                "#edge1",
                new Point3(0.0, 0.0, 0.0),
                new Point3(1.0, 0.0, 0.0)
        );

        OrientedEdgeGeom orientedEdge = new OrientedEdgeGeom("#oe1", edge, true);

        List<Point3> sampled = List.of(
                new Point3(0.0, 0.0, 0.0),
                new Point3(0.5, 0.0, 0.0),
                new Point3(1.0, 0.0, 0.0)
        );
        curveDiscretizer.stubResult(sampled);

        List<Point3> result = edgeDiscretizer.discretize(orientedEdge, null);

        assertSame(edge, curveDiscretizer.recordedEdge());
        assertEquals(sampled, result);
    }

    @Test
    void discretize_preservesOrder_whenOrientationIsTrue() {
        RecordingCurveDiscretizer curveDiscretizer = new RecordingCurveDiscretizer();
        DefaultEdgeDiscretizer edgeDiscretizer = new DefaultEdgeDiscretizer(curveDiscretizer);

        EdgeGeom edge = edgeGeom(
                "#edge2",
                new Point3(0.0, 0.0, 0.0),
                new Point3(2.0, 0.0, 0.0)
        );

        OrientedEdgeGeom orientedEdge = new OrientedEdgeGeom("#oe2", edge, true);

        Point3 p1 = new Point3(0.0, 0.0, 0.0);
        Point3 p2 = new Point3(1.0, 0.0, 0.0);
        Point3 p3 = new Point3(2.0, 0.0, 0.0);

        curveDiscretizer.stubResult(List.of(p1, p2, p3));

        List<Point3> result = edgeDiscretizer.discretize(orientedEdge, null);

        assertEquals(List.of(p1, p2, p3), result);
    }

    @Test
    void discretize_reversesOrder_whenOrientationIsFalse() {
        RecordingCurveDiscretizer curveDiscretizer = new RecordingCurveDiscretizer();
        DefaultEdgeDiscretizer edgeDiscretizer = new DefaultEdgeDiscretizer(curveDiscretizer);

        EdgeGeom edge = edgeGeom(
                "#edge3",
                new Point3(0.0, 0.0, 0.0),
                new Point3(2.0, 0.0, 0.0)
        );

        OrientedEdgeGeom orientedEdge = new OrientedEdgeGeom("#oe3", edge, false);

        Point3 p1 = new Point3(0.0, 0.0, 0.0);
        Point3 p2 = new Point3(1.0, 0.0, 0.0);
        Point3 p3 = new Point3(2.0, 0.0, 0.0);

        curveDiscretizer.stubResult(List.of(p1, p2, p3));

        List<Point3> result = edgeDiscretizer.discretize(orientedEdge, null);

        assertEquals(List.of(p3, p2, p1), result);
    }

    private EdgeGeom edgeGeom(String stepId, Point3 start, Point3 end) {
        return new EdgeGeom(
                stepId,
                new LineCurve3(start, UnitVec3.of(new Vec3(1.0, 0.0, 0.0))),
                new VertexGeom(stepId + "_start", start),
                new VertexGeom(stepId + "_end", end),
                true
        );
    }

    private static final class RecordingCurveDiscretizer implements CurveDiscretizer {
        private EdgeGeom recordedEdge;
        private List<Point3> stubResult = List.of();

        @Override
        public List<Point3> discretize(EdgeGeom edge) {
            this.recordedEdge = edge;
            return new ArrayList<>(stubResult);
        }

        void stubResult(List<Point3> points) {
            this.stubResult = new ArrayList<>(points);
        }

        EdgeGeom recordedEdge() {
            return recordedEdge;
        }
    }
}