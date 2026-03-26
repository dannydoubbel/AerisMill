package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EdgeLoopParserTest {
    @Test
    void parseEdgeLoop_shouldParseCorrectly() {
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

        OrientedEdge orientedEdge1 = new OrientedEdge(
                "#60",
                "( 'NONE', *, *, #50, .T. )",
                "NONE",
                null,
                null,
                edgeCurve,
                true
        );

        OrientedEdge orientedEdge2 = new OrientedEdge(
                "#61",
                "( 'NONE', *, *, #50, .F. )",
                "NONE",
                null,
                null,
                edgeCurve,
                false
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#60", orientedEdge1);
        parsedEntities.put("#61", orientedEdge2);

        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.EDGE_LOOP,
                "( 'NONE', ( #60, #61 ) )"
        );

        List<String> params = List.of("'NONE'", "( #60, #61 )");

        EdgeLoopParser parser = new EdgeLoopParser();
        EdgeLoop result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', ( #60, #61 ) )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(2, result.getEdgeList().size());
        assertEquals(orientedEdge1, result.getEdgeList().get(0));
        assertEquals(orientedEdge2, result.getEdgeList().get(1));
    }
}