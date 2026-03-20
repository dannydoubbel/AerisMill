package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.*;

class AdvancedBrepShapeRepresentationParserTest {
    @Test
    void parseAdvancedBrepShapeRepresentation_shouldParseCorrectly() {
        ManifoldSolidBrep brep = new ManifoldSolidBrep(
                "#10",
                "( 'NONE', #30 )",
                "NONE",
                null
        );

        StepEntity context = new StepEntity(
                "#20",
                 StepEntityType.GEOMETRIC_REPRESENTATION_CONTEXT.getName(),
                "( 3 )"
        );

        Map<String, Object> parsedEntities = new HashMap<>();
        parsedEntities.put("#10", brep);
        parsedEntities.put("#20", context);

        StepEntity entity = new StepEntity(
                "#100",
                 StepEntityType.ADVANCED_BREP_SHAPE_REPRESENTATION.getName(),
                "( 'NONE', ( #10 ), #20 )"
        );

        List<String> params = List.of("'NONE'", "( #10 )", "#20");

        AdvancedBrepShapeRepresentationParser parser = new AdvancedBrepShapeRepresentationParser();
        AdvancedBrepShapeRepresentation result = parser.parse(entity, params, parsedEntities);

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', ( #10 ), #20 )", result.getRawParameters());
        assertEquals("NONE", result.getName());
        assertEquals(1, result.getItems().size());
        assertEquals(brep, result.getItems().get(0));
        assertEquals(context, result.getContextOfItems());
    }
}