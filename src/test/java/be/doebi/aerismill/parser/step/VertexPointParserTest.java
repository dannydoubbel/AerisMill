package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VertexPointParserTest {

    @Test
    void parseVertexPoint_shouldParseCorrectly() {
        CartesianPoint point = new CartesianPoint(
                "#10",
                "( 'NONE', ( 1.0, 2.0, 3.0 ) )",
                "NONE",
                List.of(1.0, 2.0, 3.0)
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#10", point);

        StepEntity entity = new StepEntity(
                "#100",
                "VERTEX_POINT",
                "( 'NONE', #10 )"
        );

        List<String> params = List.of("'NONE'", "#10");

        VertexPointParser parser = new VertexPointParser();
        VertexPoint result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(point, result.getVertexGeometry());
    }
}