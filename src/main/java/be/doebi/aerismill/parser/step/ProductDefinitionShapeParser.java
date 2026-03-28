package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.definition.ProductDefinitionShape;

import java.util.List;
import java.util.Map;

public class ProductDefinitionShapeParser implements EntityParser<ProductDefinitionShape> {

    @Override
    public ProductDefinitionShape parse(StepEntity entity, List<String> params, Map<String, Object> context) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String description = StepParserUtils.parseStepString(params.get(1));
        String definitionRef = parseStepReference(params.get(2));

        return new ProductDefinitionShape(
                entity.getId(),
                entity.getType(),
                entity.getRawParameters(),
                name,
                description,
                definitionRef
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.PRODUCT_DEFINITION_SHAPE, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }

    private String parseStepReference(String token) {
        if (token == null) {
            return null;
        }

        String trimmed = token.trim();
        if (trimmed.equals("$") || trimmed.equals("*")) {
            return null;
        }

        return trimmed;
    }
}