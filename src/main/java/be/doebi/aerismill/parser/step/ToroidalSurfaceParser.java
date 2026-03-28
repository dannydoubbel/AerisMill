package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.ToroidalSurface;

import java.util.List;
import java.util.Map;

public class ToroidalSurfaceParser implements EntityParser<ToroidalSurface>  {

    @Override
    public ToroidalSurface parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String positionRef = parseStepReference(params.get(1));
        double majorRadius = Double.parseDouble(params.get(2).trim());
        double minorRadius = Double.parseDouble(params.get(3).trim());

        return new ToroidalSurface(
                entity.getId(),
                entity.getRawParameters(),
                name,
                positionRef,
                majorRadius,
                minorRadius
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.TOROIDAL_SURFACE, rawParameters);
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