package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.FaceBound;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FaceBoundParserTest {
    @Test
    void parseFaceBound_shouldParseCorrectly() {
        EdgeLoop edgeLoop = new EdgeLoop(
                "#10",
                "( 'NONE', ( #60, #61 ) )",
                "NONE",
                List.of()
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#10", edgeLoop);

        StepEntity entity = new StepEntity(
                "#100",
                "FACE_BOUND",
                "( 'NONE', #10, .T. )"
        );

        List<String> params = List.of("'NONE'", "#10", ".T.");

        FaceBoundParser parser = new FaceBoundParser();
        FaceBound result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10, .T. )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(edgeLoop, result.getBound());
        assertTrue(result.isOrientation());
    }
}