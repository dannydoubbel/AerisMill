package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EdgeCurveParserTest {

    @Test
    void parseEdgeCurve_shouldParseCorrectly() {
        Map<String, Object> parsedEntities = new HashMap<>();

        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.EDGE_CURVE,
                "( 'NONE', #20, #21, #40, .T. )"
        );

        List<String> params = List.of("'NONE'", "#20", "#21", "#40", ".T.");

        EdgeCurveParser parser = new EdgeCurveParser();
        EdgeCurve result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #20, #21, #40, .T. )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#20", result.getEdgeStartRef());
        assertEquals("#21", result.getEdgeEndRef());
        assertEquals("#40", result.getEdgeGeometryRef());
        assertTrue(result.isSameSense());
    }
}