package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.geometry.Direction;
import org.junit.jupiter.api.Test;

import static be.doebi.aerismill.model.step.base.StepEntityType.DIRECTION;
import static org.junit.jupiter.api.Assertions.*;

class DirectionParserTest {

    @Test
    void parsesDirection() {
        String id = "#42";
        String rawParameters = "'NONE', ( 0.0, 0.0, 1.0 )";

        Direction direction = DirectionParser.parse(id, rawParameters);

        assertEquals("#42", direction.getId());
        assertEquals(DIRECTION.getName(), direction.getType());
        assertEquals("NONE", direction.getName());

        assertEquals(3, direction.getDirectionRatios().size());
        assertEquals(0.0, direction.getX());
        assertEquals(0.0, direction.getY());
        assertEquals(1.0, direction.getZ());
    }
}