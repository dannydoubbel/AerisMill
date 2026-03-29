package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.SphericalSurface;

import java.util.List;
import java.util.Map;

public class SphericalSurfaceParser implements EntityParser<SphericalSurface> {

    @Override
    public SphericalSurface parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String positionRef = params.get(1).trim();
        double radius = Double.parseDouble(params.get(2).trim());

        return new SphericalSurface(
                entity.getId(),
                entity.getRawParameters(),
                name,
                positionRef,
                radius
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.SPHERICAL_SURFACE, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}