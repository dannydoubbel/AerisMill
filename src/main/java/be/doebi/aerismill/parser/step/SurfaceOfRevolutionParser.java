package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.SurfaceOfRevolution;

import java.util.List;
import java.util.Map;

public class SurfaceOfRevolutionParser implements EntityParser<SurfaceOfRevolution> {

    @Override
    public SurfaceOfRevolution parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String sweptCurveRef = parseStepReference(params.get(1));
        String axisPositionRef = parseStepReference(params.get(2));

        return new SurfaceOfRevolution(
                entity.getId(),
                entity.getRawParameters(),
                name,
                sweptCurveRef,
                axisPositionRef
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.SURFACE_OF_REVOLUTION, rawParameters);
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