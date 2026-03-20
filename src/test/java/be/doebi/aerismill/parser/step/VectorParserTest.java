package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.geometry.Direction;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class VectorParserTest {
    @Test
    void parseVector_shouldParseCorrectly() {
        Direction direction = new Direction(
                "#20",
                "( 'NONE', ( 1.0, 0.0, 0.0 ) )",
                "NONE",
                List.of(1.0, 0.0, 0.0)
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#20", direction);

        StepEntity entity = new StepEntity(
                "#100",
                "VECTOR",
                "( 'NONE', #20, 5.0 )"
        );

        List<String> params = List.of("'NONE'", "#20", "5.0");

        VectorParser parser = new VectorParser();
        Vector result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #20, 5.0 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(direction, result.getOrientation());
        assertEquals(5.0, result.getMagnitude());
    }
}