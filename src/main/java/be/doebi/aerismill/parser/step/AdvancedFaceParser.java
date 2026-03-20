package be.doebi.aerismill.parser.step;
import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;
import be.doebi.aerismill.model.step.topology.AdvancedFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class AdvancedFaceParser implements EntityParser<AdvancedFace>   {
    public AdvancedFace parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        List<StepEntity> bounds = resolveStepEntityList(params.get(1), parsedEntities);
        StepEntity faceGeometry = resolveStepEntity(params.get(2), parsedEntities);
        boolean sameSense = parseStepBoolean(params.get(3));

        return new AdvancedFace(
                entity.getId(),
                entity.getRawParameters(),
                name,
                bounds,
                faceGeometry,
                sameSense
        );
    }



    private boolean parseStepBoolean(String token) {
        return ".T.".equalsIgnoreCase(token.trim());
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
