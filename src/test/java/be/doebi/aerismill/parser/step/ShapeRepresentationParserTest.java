package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.representation.ShapeRepresentation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShapeRepresentationParserTest {

    @Test
    void parsesShapeRepresentation() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.SHAPE_REPRESENTATION,
                "('Body', (#10, #11, #12), #200)"
        );

        List<String> params = List.of("'Body'", "(#10, #11, #12)", "#200");

        ShapeRepresentationParser parser = new ShapeRepresentationParser();
        ShapeRepresentation result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("SHAPE_REPRESENTATION", result.getType().getName());
        assertEquals("Body", result.getName());
        assertEquals(List.of("#10", "#11", "#12"), result.getItemRefs());
        assertEquals("#200", result.getContextRef());
    }
}