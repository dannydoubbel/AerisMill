package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.ConicalSurface;

import java.util.List;
import java.util.Map;

public class ConicalSurfaceParser implements EntityParser<ConicalSurface> {

    @Override
    public ConicalSurface parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String positionRef = params.get(1).trim();
        double radius = Double.parseDouble(params.get(2).trim());
        double semiAngle = Double.parseDouble(params.get(3).trim());

        return new ConicalSurface(
                entity.getId(),
                entity.getRawParameters(),
                name,
                positionRef,
                radius,
                semiAngle
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.CONICAL_SURFACE, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}