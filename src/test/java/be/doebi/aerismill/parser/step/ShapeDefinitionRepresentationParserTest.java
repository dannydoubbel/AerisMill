package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.definition.ShapeDefinitionRepresentation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShapeDefinitionRepresentationParserTest {

    @Test
    void parsesShapeDefinitionRepresentation() {
        StepEntity entity = new StepEntity(
                "#123",
                "SHAPE_DEFINITION_REPRESENTATION",
                "(#500, #600)"
        );

        List<String> params = List.of("#500", "#600");

        be.doebi.aerismill.parser.step.ShapeDefinitionRepresentationParser parser = new be.doebi.aerismill.parser.step.ShapeDefinitionRepresentationParser();
        ShapeDefinitionRepresentation result = parser.parse(entity, params, Map.of());

        assertEquals("#123", result.getId());
        assertEquals("SHAPE_DEFINITION_REPRESENTATION", result.getType());
        assertEquals("#500", result.getDefinitionRef());
        assertEquals("#600", result.getRepresentationRef());
    }
}