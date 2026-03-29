package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.parser.step.geometry.CartesianPointParser;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CartesianPointParserTest {

    @Test
    void parsesCartesianPoint() {
        StepEntity entity = new StepEntity(
                "#123",
                StepEntityType.CARTESIAN_POINT,
                "('NONE', ( 1.0, 2.5, 3.75 ))"
        );

        List<String> params = List.of("'NONE'", "( 1.0, 2.5, 3.75 )");

        CartesianPointParser parser = new CartesianPointParser();
        CartesianPoint point = parser.parse(entity, params, Map.of());

        assertEquals("#123", point.getId());
        assertEquals(StepEntityType.CARTESIAN_POINT, point.getType());
        assertEquals("NONE", point.getName());
        assertEquals(3, point.getCoordinates().size());
        assertEquals(1.0, point.getX());
        assertEquals(2.5, point.getY());
        assertEquals(3.75, point.getZ());
    }
}