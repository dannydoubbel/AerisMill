package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.definition.ShapeDefinitionRepresentation;

import java.util.List;
import java.util.Map;

public class ShapeDefinitionRepresentationParser implements EntityParser<ShapeDefinitionRepresentation> {

    @Override
    public ShapeDefinitionRepresentation parse(StepEntity entity, List<String> params, Map<String, Object> context) {
        String definitionRef = parseStepReference(params.get(0));
        String representationRef = parseStepReference(params.get(1));

        return new ShapeDefinitionRepresentation(
                entity.getId(),
                entity.getType(),
                entity.getRawParameters(),
                definitionRef,
                representationRef
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.SHAPE_DEFINITION_REPRESENTATION, rawParameters);
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