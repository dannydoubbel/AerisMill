package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.FaceOuterBound;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FaceOuterBoundParserTest {
    @Test
    void parseFaceOuterBound_shouldParseCorrectly() {
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
                "FACE_OUTER_BOUND",
                "( 'NONE', #10, .T. )"
        );

        List<String> params = List.of("'NONE'", "#10", ".T.");

        FaceOuterBoundParser parser = new FaceOuterBoundParser();
        FaceOuterBound result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10, .T. )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(edgeLoop, result.getBound());
        assertTrue(result.isOrientation());
    }
}