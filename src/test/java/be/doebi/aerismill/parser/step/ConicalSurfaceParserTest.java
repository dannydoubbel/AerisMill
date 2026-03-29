package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.ConicalSurface;
import be.doebi.aerismill.parser.step.geometry.ConicalSurfaceParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConicalSurfaceParserTest {
    @Test
    void parseConicalSurface_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.CONICAL_SURFACE,
                "( 'NONE', #40, 25.0, 0.7853981633974483 )"
        );

        List<String> params = List.of("'NONE'", "#40", "25.0", "0.7853981633974483");

        ConicalSurfaceParser parser = new ConicalSurfaceParser();
        ConicalSurface result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #40, 25.0, 0.7853981633974483 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#40", result.getPositionRef());
        assertNull(result.getPosition());
        assertEquals(25.0, result.getRadius(), 0.000001);
        assertEquals(0.7853981633974483, result.getSemiAngle(), 0.000001);
    }
}