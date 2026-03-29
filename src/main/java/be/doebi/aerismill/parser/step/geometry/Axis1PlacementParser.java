package be.doebi.aerismill.parser.step.geometry;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Axis1Placement;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class Axis1PlacementParser implements EntityParser<Axis1Placement> {

    @Override
    public Axis1Placement parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String locationRef = parseStepReference(params.get(1));
        String axisRef = parseStepReference(params.get(2));

        return new Axis1Placement(
                entity.getId(),
                entity.getRawParameters(),
                name,
                locationRef,
                axisRef
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.AXIS1_PLACEMENT, rawParameters);
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