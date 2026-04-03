package be.doebi.aerismill.tessellation.curve;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.model.geom.topology.VertexGeom;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefaultEdgeDiscretizerTest {

    @Test
    void discretize_returnsOrientedStartAndEnd() {
        Point3 p1 = new Point3(1.0, 2.0, 3.0);
        Point3 p2 = new Point3(4.0, 5.0, 6.0);

        VertexGeom v1 = new VertexGeom("#v1", p1);
        VertexGeom v2 = new VertexGeom("#v2", p2);

        EdgeGeom edge = new EdgeGeom(
                "#e1",
                null,
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
                null,
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
}