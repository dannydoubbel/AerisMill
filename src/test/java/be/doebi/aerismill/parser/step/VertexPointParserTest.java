package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import be.doebi.aerismill.parser.step.topology.VertexPointParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VertexPointParserTest {

    @Test
    void parseVertexPoint_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.VERTEX_POINT,
                "( 'NONE', #10 )"
        );

        List<String> params = List.of("'NONE'", "#10");

        VertexPointParser parser = new VertexPointParser();
        VertexPoint result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#10", result.getVertexGeometryRef());
        assertNull(result.getVertexGeometry());
    }
}