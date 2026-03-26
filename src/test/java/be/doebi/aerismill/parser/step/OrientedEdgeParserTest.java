package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrientedEdgeParserTest {
    @Test
    void parseOrientedEdge_shouldParseCorrectly() {
        CartesianPoint p1 = new CartesianPoint(
                "#10",
                "( 'NONE', ( 0.0, 0.0, 0.0 ) )",
                "NONE",
                List.of(0.0, 0.0, 0.0)
        );

        CartesianPoint p2 = new CartesianPoint(
                "#11",
                "( 'NONE', ( 10.0, 0.0, 0.0 ) )",
                "NONE",
                List.of(10.0, 0.0, 0.0)
        );

        VertexPoint v1 = new VertexPoint(
                "#20",
                "( 'NONE', #10 )",
                "NONE",
                p1
        );

        VertexPoint v2 = new VertexPoint(
                "#21",
                "( 'NONE', #11 )",
                "NONE",
                p2
        );

        Direction direction = new Direction(
                "#30",
                "( 'NONE', ( 1.0, 0.0, 0.0 ) )",
                "NONE",
                List.of(1.0, 0.0, 0.0)
        );

        Vector vector = new Vector(
                "#31",
                "( 'NONE', #30, 10.0 )",
                "NONE",
                direction,
                10.0
        );

        Line line = new Line(
                "#40",
                "( 'NONE', #10, #31 )",
                "NONE",
                p1,
                vector
        );

        EdgeCurve edgeCurve = new EdgeCurve(
                "#50",
                "( 'NONE', #20, #21, #40, .T. )",
                "NONE",
                v1,
                v2,
                line,
                true
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#20", v1);
        parsedEntities.put("#21", v2);
        parsedEntities.put("#50", edgeCurve);

        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.ORIENTED_EDGE,
                "( 'NONE', *, *, #50, .T. )"
        );

        List<String> params = List.of("'NONE'", "*", "*", "#50", ".T.");

        OrientedEdgeParser parser = new OrientedEdgeParser();
        OrientedEdge result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', *, *, #50, .T. )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertNull(result.getEdgeStart());
        assertNull(result.getEdgeEnd());
        assertEquals(edgeCurve, result.getEdgeElement());
        assertTrue(result.isOrientation());
    }
}