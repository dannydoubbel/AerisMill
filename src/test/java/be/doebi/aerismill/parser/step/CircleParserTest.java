package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Circle;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CircleParserTest {
    @Test
    void parseCircle_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.CIRCLE,
                "( 'NONE', #40, 25.0 )"
        );

        List<String> params = List.of("'NONE'", "#40", "25.0");

        CircleParser parser = new CircleParser();
        Circle result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #40, 25.0 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#40", result.getPositionRef());
        assertNull(result.getPosition());
        assertEquals(25.0, result.getRadius(), 0.000001);
    }
}