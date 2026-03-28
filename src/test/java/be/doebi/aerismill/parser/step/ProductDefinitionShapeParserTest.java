package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.definition.ProductDefinitionShape;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductDefinitionShapeParserTest {

    @Test
    void parsesProductDefinitionShape() {
        StepEntity entity = new StepEntity(
                "#700",
                StepEntityType.PRODUCT_DEFINITION_SHAPE,
                "('Shape Name', 'Shape Description', #800)"
        );

        List<String> params = List.of("'Shape Name'", "'Shape Description'", "#800");

        ProductDefinitionShapeParser parser = new ProductDefinitionShapeParser();
        ProductDefinitionShape result = parser.parse(entity, params, Map.of());

        assertEquals("#700", result.getId());
        assertEquals(StepEntityType.PRODUCT_DEFINITION_SHAPE, result.getType());
        assertEquals("Shape Name", result.getName());
        assertEquals("Shape Description", result.getDescription());
        assertEquals("#800", result.getDefinitionRef());
    }
}