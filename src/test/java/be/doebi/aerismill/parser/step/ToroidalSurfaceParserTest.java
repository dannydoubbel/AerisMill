package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.ToroidalSurface;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ToroidalSurfaceParserTest {
    @Test
    void parseToroidalSurface_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.TOROIDAL_SURFACE,
                "( 'NONE', #40, 30.0, 5.0 )"
        );

        List<String> params = List.of("'NONE'", "#40", "30.0", "5.0");

        ToroidalSurfaceParser parser = new ToroidalSurfaceParser();
        ToroidalSurface result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #40, 30.0, 5.0 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#40", result.getPositionRef());
        assertNull(result.getPosition());
        assertEquals(30.0, result.getMajorRadius(), 0.000001);
        assertEquals(5.0, result.getMinorRadius(), 0.000001);
    }
}