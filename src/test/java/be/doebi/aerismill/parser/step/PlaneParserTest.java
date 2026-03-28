package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Plane;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlaneParserTest {
    @Test
    void parsePlane_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.PLANE,
                "( 'NONE', #40 )"
        );

        List<String> params = List.of("'NONE'", "#40");

        PlaneParser parser = new PlaneParser();
        Plane result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #40 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#40", result.getPositionRef());
        assertNull(result.getPosition());
    }
}