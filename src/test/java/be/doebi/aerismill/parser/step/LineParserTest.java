package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Line;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LineParserTest {
    @Test
    void parseLine_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.LINE,
                "( 'NONE', #10, #30 )"
        );

        List<String> params = List.of("'NONE'", "#10", "#30");

        LineParser parser = new LineParser();
        Line result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', #10, #30 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals("#10", result.getPointRef());
        assertEquals("#30", result.getVectorRef());
        assertNull(result.getPoint());
        assertNull(result.getVector());
    }
}