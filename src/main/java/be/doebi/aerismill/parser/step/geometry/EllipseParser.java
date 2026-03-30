package be.doebi.aerismill.parser.step.geometry;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Ellipse;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class EllipseParser implements EntityParser<Ellipse> {

    @Override
    public Ellipse parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String positionRef = parseStepReference(params.get(1));
        double semiAxis1 = Double.parseDouble(params.get(2).trim());
        double semiAxis2 = Double.parseDouble(params.get(3).trim());

        return new Ellipse(
                entity.getId(),
                entity.getRawParameters(),
                name,
                positionRef,
                semiAxis1,
                semiAxis2
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.ELLIPSE, rawParameters);
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