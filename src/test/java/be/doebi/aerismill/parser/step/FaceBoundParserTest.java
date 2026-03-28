package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.FaceBound;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FaceBoundParserTest {
    @Test
    void parseFaceBound_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.FACE_BOUND,
                "( 'NONE', #10, .T. )"
        );

        List<String> params = List.of("'NONE'", "#10", ".T.");

        FaceBoundParser parser = new FaceBoundParser();
        FaceBound result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10, .T. )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#10", result.getBoundRef());
        assertNull(result.getBound());
        assertTrue(result.isOrientation());
    }
}