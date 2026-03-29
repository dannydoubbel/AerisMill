package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.parser.step.topology.AdvancedFaceParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdvancedFaceParserTest {
    @Test
    void parseAdvancedFace_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.ADVANCED_FACE,
                "( 'NONE', ( #20 ), #40, .T. )"
        );

        List<String> params = List.of("'NONE'", "( #20 )", "#40", ".T.");

        AdvancedFaceParser parser = new AdvancedFaceParser();
        AdvancedFace result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', ( #20 ), #40, .T. )", result.getRawParameters());
        assertEquals("NONE", result.getName());

        assertEquals(1, result.getBoundRefs().size());
        assertEquals("#20", result.getBoundRefs().get(0));
        assertEquals("#40", result.getFaceGeometryRef());

        assertTrue(result.getBounds().isEmpty());
        assertNull(result.getFaceGeometry());
        assertTrue(result.isSameSense());
    }
}