package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class Axis2Placement3DParserTest {
    @Test
    void parseAxis2Placement3D_shouldParseCorrectly() {
        List<String> params = List.of("'NONE'", "#10", "#20", "#30");

        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.AXIS2_PLACEMENT_3D,
                "( 'NONE', #10, #20, #30 )"
        );

        Axis2Placement3DParser parser = new Axis2Placement3DParser();
        Axis2Placement3D result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("NONE", result.getName());

        assertEquals("#10", result.getLocationRef());
        assertEquals("#20", result.getAxisRef());
        assertEquals("#30", result.getRefDirectionRef());

        assertNull(result.getLocation());
        assertNull(result.getAxis());
        assertNull(result.getRefDirection());
    }
}