package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EdgeCurveParserTest {
    @Test
    void parseEdgeCurve_shouldParseCorrectly() {
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

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#20", v1);
        parsedEntities.put("#21", v2);
        parsedEntities.put("#40", line);

        StepEntity entity = new StepEntity(
                "#100",
                "EDGE_CURVE",
                "( 'NONE', #20, #21, #40, .T. )"
        );

        List<String> params = List.of("'NONE'", "#20", "#21", "#40", ".T.");

        EdgeCurveParser parser = new EdgeCurveParser();
        EdgeCurve result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #20, #21, #40, .T. )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(v1, result.getEdgeStart());
        assertEquals(v2, result.getEdgeEnd());
        assertEquals(line, result.getEdgeGeometry());
        assertTrue(result.isSameSense());
    }
}