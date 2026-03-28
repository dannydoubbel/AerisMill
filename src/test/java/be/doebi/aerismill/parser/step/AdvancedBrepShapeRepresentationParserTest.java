package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdvancedBrepShapeRepresentationParserTest {
    @Test
    void parseAdvancedBrepShapeRepresentation_shouldParseCorrectly() {
        StepEntity entity = new StepEntity(
                "#100",
                StepEntityType.ADVANCED_BREP_SHAPE_REPRESENTATION,
                "( 'NONE', ( #10 ), #20 )"
        );

        List<String> params = List.of("'NONE'", "( #10 )", "#20");

        AdvancedBrepShapeRepresentationParser parser = new AdvancedBrepShapeRepresentationParser();
        AdvancedBrepShapeRepresentation result = parser.parse(entity, params, Map.of());

        assertEquals("#100", result.getId());
        assertEquals("( 'NONE', ( #10 ), #20 )", result.getRawParameters());
        assertEquals("NONE", result.getName());

        assertEquals(1, result.getItemRefs().size());
        assertEquals("#10", result.getItemRefs().get(0));
        assertEquals("#20", result.getContextOfItemsRef());

        assertNull(result.getItems());
        assertNull(result.getContextOfItems());
    }
}