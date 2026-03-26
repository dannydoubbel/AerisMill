package be.doebi.aerismill.parser.step;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedBrepShapeRepresentationParser implements EntityParser<AdvancedBrepShapeRepresentation> {
    @Override
    public AdvancedBrepShapeRepresentation parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        List<StepEntity> items = resolveStepEntityList(params.get(1), parsedEntities);
        StepEntity contextOfItems = resolveStepEntity(params.get(2), parsedEntities);

        return new AdvancedBrepShapeRepresentation(
                entity.getId(),
                entity.getRawParameters(),
                name,
                items,
                contextOfItems
        );
    }



    private StepEntity resolveStepEntity(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$") || token.equals("*")) {
            return null;
        }
        return (StepEntity) parsedEntities.get(token);
    }

    private List<StepEntity> resolveStepEntityList(String token, Map<String, Object> parsedEntities) {
        List<StepEntity> result = new ArrayList<>();

        if (token == null || token.equals("$")) {
            return result;
        }

        String trimmed = token.trim();

        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        if (trimmed.isBlank()) {
            return result;
        }

        String[] refs = trimmed.split(",");

        for (String ref : refs) {
            String cleanRef = ref.trim();
            StepEntity resolved = (StepEntity) parsedEntities.get(cleanRef);
            if (resolved != null) {
                result.add(resolved);
            }
        }

        return result;
    }
}
