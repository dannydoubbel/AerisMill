package be.doebi.aerismill.parser.step.representation;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class AdvancedBrepShapeRepresentationParser implements EntityParser<AdvancedBrepShapeRepresentation> {

    @Override
    public AdvancedBrepShapeRepresentation parse(StepEntity entity, List<String> params, Map<String, Object> context) {
        String name = StepParserUtils.parseStepString(params.get(0));
        List<String> itemRefs = StepParserUtils.parseReferenceList(params.get(1));
        String contextOfItemsRef = parseStepReference(params.get(2));

        return new AdvancedBrepShapeRepresentation(
                entity.getId(),
                entity.getRawParameters(),
                name,
                itemRefs,
                contextOfItemsRef
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.ADVANCED_BREP_SHAPE_REPRESENTATION, rawParameters);
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