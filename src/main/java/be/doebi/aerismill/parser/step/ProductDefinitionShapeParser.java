package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.definition.ProductDefinitionShape;

import java.util.List;
import java.util.Map;

public class ProductDefinitionShapeParser implements EntityParser<ProductDefinitionShape> {

    @Override
    public ProductDefinitionShape parse(StepEntity entity, List<String> params, Map<String, Object> context) {
        String name = unquote(params.get(0));
        String description = unquote(params.get(1));
        String definitionRef = params.get(2);

        return new ProductDefinitionShape(
                entity.getId(),
                entity.getType(),
                entity.getRawParameters(),
                name,
                description,
                definitionRef
        );
    }

    private String unquote(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}