package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EdgeLoopParserTest {

    @Test
    void parseEdgeLoop_shouldParseCorrectly() {
        Map<String, Object> parsedEntities = new HashMap<>();

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
        assertEquals(2, result.getEdgeListRefs().size());
        assertEquals("#60", result.getEdgeListRefs().get(0));
        assertEquals("#61", result.getEdgeListRefs().get(1));
    }
}