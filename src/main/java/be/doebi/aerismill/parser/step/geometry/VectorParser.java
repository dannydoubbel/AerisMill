package be.doebi.aerismill.parser.step.geometry;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class VectorParser implements EntityParser<Vector> {

    @Override
    public Vector parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String orientationRef = params.get(1).trim();
        double magnitude = Double.parseDouble(params.get(2).trim());

        return new Vector(
                entity.getId(),
                entity.getRawParameters(),
                name,
                orientationRef,
                magnitude
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.VECTOR, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}