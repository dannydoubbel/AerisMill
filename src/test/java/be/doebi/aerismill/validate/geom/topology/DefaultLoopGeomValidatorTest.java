package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.model.geom.topology.VertexGeom;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultLoopGeomValidatorTest {
    private final DefaultLoopGeomValidator validator = new DefaultLoopGeomValidator();
    @Test
    void validate_nullLoop_returnsLoopNullError() {
        ValidationReport report = validator.validate(null);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.LOOP_NULL, report.messages().getFirst().code());
    }

    @Test
    void validate_loopWithNullEdge_returnsLoopEdgeNullError() {
        List<OrientedEdgeGeom> edges = new ArrayList<>();
        edges.add(null);

        LoopGeom loop = new LoopGeom("#100", edges);

        ValidationReport report = validator.validate(loop);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.LOOP_EDGE_NULL, report.messages().get(0).code());
    }

    @Test
    void validate_loopWithEdgeHavingNullStart_returnsLoopEdgeStartNullError() {
        VertexGeom endVertex = new VertexGeom("#3", new Point3(1.0, 0.0, 0.0));

        EdgeGeom edge = new EdgeGeom(
                "#2",
                null,
                null,
                endVertex,
                true
        );

        OrientedEdgeGeom orientedEdge = new OrientedEdgeGeom("#1", edge, true);

        LoopGeom loop = new LoopGeom(
                "#100",
                List.of(orientedEdge)
        );

        ValidationReport report = validator.validate(loop);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.LOOP_EDGE_START_NULL, report.messages().get(0).code());
    }

    @Test
    void validate_loopWithEdgeHavingNullEnd_returnsLoopEdgeEndNullError() {
        VertexGeom startVertex = new VertexGeom("#3", new Point3(0.0, 0.0, 0.0));

        EdgeGeom edge = new EdgeGeom(
                "#2",
                null,
                startVertex,
                null,
                true
        );

        OrientedEdgeGeom orientedEdge = new OrientedEdgeGeom("#1", edge, true);

        LoopGeom loop = new LoopGeom(
                "#100",
                List.of(orientedEdge)
        );

        ValidationReport report = validator.validate(loop);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.LOOP_EDGE_END_NULL, report.messages().get(0).code());
    }

    @Test
    void validate_loopWithBrokenContinuity_returnsLoopContinuityBrokenError() {
        VertexGeom v1 = new VertexGeom("#1", new Point3(0.0, 0.0, 0.0));
        VertexGeom v2 = new VertexGeom("#2", new Point3(1.0, 0.0, 0.0));
        VertexGeom v3 = new VertexGeom("#3", new Point3(2.0, 0.0, 0.0));
        VertexGeom v4 = new VertexGeom("#4", new Point3(3.0, 0.0, 0.0));

        EdgeGeom edge1 = new EdgeGeom("#10", null, v1, v2, true);
        EdgeGeom edge2 = new EdgeGeom("#11", null, v3, v4, true);

        OrientedEdgeGeom oe1 = new OrientedEdgeGeom("#20", edge1, true);
        OrientedEdgeGeom oe2 = new OrientedEdgeGeom("#21", edge2, true);

        LoopGeom loop = new LoopGeom("#100", List.of(oe1, oe2));

        ValidationReport report = validator.validate(loop);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertTrue(report.messages().stream()
                .anyMatch(message -> message.code() == ValidationCode.LOOP_CONTINUITY_BROKEN));
    }

    @Test
    void validate_loopNotClosed_returnsLoopNotClosedError() {
        VertexGeom v1 = new VertexGeom("#1", new Point3(0.0, 0.0, 0.0));
        VertexGeom v2 = new VertexGeom("#2", new Point3(1.0, 0.0, 0.0));
        VertexGeom v3 = new VertexGeom("#3", new Point3(2.0, 0.0, 0.0));

        EdgeGeom edge1 = new EdgeGeom("#10", null, v1, v2, true);
        EdgeGeom edge2 = new EdgeGeom("#11", null, v2, v3, true);

        OrientedEdgeGeom oe1 = new OrientedEdgeGeom("#20", edge1, true);
        OrientedEdgeGeom oe2 = new OrientedEdgeGeom("#21", edge2, true);

        LoopGeom loop = new LoopGeom("#100", List.of(oe1, oe2));

        ValidationReport report = validator.validate(loop);

        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.messages().size());
        assertEquals(ValidationCode.LOOP_NOT_CLOSED, report.messages().get(0).code());
    }

    @Test
    void validate_closedContinuousLoop_returnsValidReport() {
        VertexGeom v1 = new VertexGeom("#1", new Point3(0.0, 0.0, 0.0));
        VertexGeom v2 = new VertexGeom("#2", new Point3(1.0, 0.0, 0.0));

        EdgeGeom edge1 = new EdgeGeom("#10", null, v1, v2, true);
        EdgeGeom edge2 = new EdgeGeom("#11", null, v2, v1, true);

        OrientedEdgeGeom oe1 = new OrientedEdgeGeom("#20", edge1, true);
        OrientedEdgeGeom oe2 = new OrientedEdgeGeom("#21", edge2, true);

        LoopGeom loop = new LoopGeom("#100", List.of(oe1, oe2));

        ValidationReport report = validator.validate(loop);

        assertTrue(report.isValid());
        assertFalse(report.hasErrors());
        assertTrue(report.messages().isEmpty());
    }




}