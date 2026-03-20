package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Plane;
import org.junit.jupiter.api.Test;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlaneParserTest {
    @Test
    void parsePlane_shouldParseCorrectly() {
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

        Axis2Placement3D placement = new Axis2Placement3D(
                "#40",
                "( 'NONE', #10, #20, #30 )",
                "NONE",
                point,
                axis,
                refDir
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#40", placement);

        StepEntity entity = new StepEntity(
                "#100",
                "PLANE",
                "( 'NONE', #40 )"
        );

        List<String> params = List.of("'NONE'", "#40");

        PlaneParser parser = new PlaneParser();
        Plane result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #40 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(placement, result.getPosition());
    }
}