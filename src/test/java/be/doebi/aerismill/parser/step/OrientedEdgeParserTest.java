package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OrientedEdgeParserTest {

    @Test
    void parseOrientedEdge_shouldParseCorrectly() {
        Map<String, Object> parsedEntities = new HashMap<>();

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
        assertNull(result.getEdgeStartRef());
        assertNull(result.getEdgeEndRef());
        assertEquals("#50", result.getEdgeElementRef());
        assertTrue(result.isOrientation());
    }
}