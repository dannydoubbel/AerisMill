package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Vector;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LineParserTest {
    @Test
    void parseLine_shouldParseCorrectly() {
        CartesianPoint point = new CartesianPoint(
                "#10",
                "( 'NONE', ( 1.0, 2.0, 3.0 ) )",
                "NONE",
                List.of(1.0, 2.0, 3.0)
        );

        Direction direction = new Direction(
                "#20",
                "( 'NONE', ( 1.0, 0.0, 0.0 ) )",
                "NONE",
                List.of(1.0, 0.0, 0.0)
        );

        Vector vector = new Vector(
                "#30",
                "( 'NONE', #20, 5.0 )",
                "NONE",
                direction,
                5.0
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#10", point);
        parsedEntities.put("#30", vector);

        StepEntity entity = new StepEntity(
                "#100",
                "LINE",
                "( 'NONE', #10, #30 )"
        );

        List<String> params = List.of("'NONE'", "#10", "#30");

        LineParser parser = new LineParser();
        Line result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10, #30 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(point, result.getPoint());
        assertEquals(vector, result.getVector());
    }
}