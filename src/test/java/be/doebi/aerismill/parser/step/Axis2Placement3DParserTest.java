package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;



class Axis2Placement3DParserTest {
    @Test
    void parseAxis2Placement3D_shouldParseCorrectly() {
        CartesianPoint point = new CartesianPoint(
                "#10",
                "( 'NONE', ( 1.0, 2.0, 3.0 ) )",
                "NONE",
                List.of(1.0, 2.0, 3.0)
        );
        Direction axis = new Direction(
                "#20",
                "( 'NONE', ( 0.0, 0.0, 1.0 ) )",
                "NONE",
                List.of(0.0, 0.0, 1.0)
        );

        Direction refDir = new Direction(
                "#30",
                "( 'NONE', ( 1.0, 0.0, 0.0 ) )",
                "NONE",
                List.of(1.0, 0.0, 0.0)
        );
        List<String> params = List.of("'NONE'", "#10", "#20", "#30");

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#10", point);
        parsedEntities.put("#20", axis);
        parsedEntities.put("#30", refDir);

        StepEntity entity = new StepEntity(
                "#100",
                "AXIS2_PLACEMENT_3D",
                "( 'NONE', #10, #20, #30 )"
        );

        Axis2Placement3DParser parser = new Axis2Placement3DParser();
        Axis2Placement3D result = parser.parse(entity, params, parsedEntities);
        assertEquals("#100", result.getId());
        assertEquals("NONE", result.getName());
        assertEquals(point, result.getLocation());
        assertEquals(axis, result.getAxis());
        assertEquals(refDir, result.getRefDirection());
    }
}