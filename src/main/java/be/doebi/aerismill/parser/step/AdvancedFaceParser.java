package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.AdvancedFace;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancedFaceParser implements EntityParser<AdvancedFace> {

    @Override
    public AdvancedFace parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        List<String> boundRefs = parseReferenceList(params.get(1));
        String faceGeometryRef = params.get(2).trim();
        boolean sameSense = StepParserUtils.parseStepBoolean(params.get(3));

        return new AdvancedFace(
                entity.getId(),
                entity.getRawParameters(),
                name,
                boundRefs,
                faceGeometryRef,
                sameSense
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.ADVANCED_FACE, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }

    private List<String> parseReferenceList(String token) {
        List<String> result = new ArrayList<>();

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
            result.add(ref.trim());
        }

        return result;
    }
}