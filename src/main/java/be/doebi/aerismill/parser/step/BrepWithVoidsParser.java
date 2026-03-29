package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.BrepWithVoids;

import java.util.List;
import java.util.Map;

public class BrepWithVoidsParser implements EntityParser<BrepWithVoids> {

    @Override
    public BrepWithVoids parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String outerRef = parseStepReference(params.get(1));
        List<String> voidRefs = StepParserUtils.parseReferenceList(params.get(2));

        return new BrepWithVoids(
                entity.getId(),
                entity.getRawParameters(),
                name,
                outerRef,
                voidRefs
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.BREP_WITH_VOIDS, rawParameters);
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