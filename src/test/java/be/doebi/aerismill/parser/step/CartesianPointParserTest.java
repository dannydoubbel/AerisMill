package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartesianPointParserTest {

    @Test
    void parsesCartesianPoint() {
        String id = "#123";
        String rawParameters = "'NONE', ( 1.0, 2.5, 3.75 )";

        CartesianPoint point = CartesianPointParser.parse(id, rawParameters);

        assertEquals("#123", point.getId());
        assertEquals("CARTESIAN_POINT", point.getType());
        assertEquals("NONE", point.getName());
        assertEquals(3, point.getCoordinates().size());
        assertEquals(1.0, point.getX());
        assertEquals(2.5, point.getY());
        assertEquals(3.75, point.getZ());
    }
}