package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.parser.step.geometry.VectorParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VectorParserTest {
    @Test
    void parseVector_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.VECTOR,
                "( 'NONE', #20, 5.0 )"
        );

        List<String> params = List.of("'NONE'", "#20", "5.0");

        VectorParser parser = new VectorParser();
        Vector result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #20, 5.0 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#20", result.getOrientationRef());
        assertNull(result.getOrientation());
        assertEquals(5.0, result.getMagnitude());
    }
}