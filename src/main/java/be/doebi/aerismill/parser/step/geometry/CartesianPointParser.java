package be.doebi.aerismill.parser.step.geometry;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class CartesianPointParser implements EntityParser<CartesianPoint> {

    @Override
    public CartesianPoint parse(
            StepEntity entity,
            List<String> params,
            Map<String, Object> parsedEntities
    ) {
        String name = StepParserUtils.parseStepString(params.get(0));
        List<Double> coordinates = StepParserUtils.parseDoubleList(params.get(1));

        return new CartesianPoint(
                entity.getId(),
                entity.getRawParameters(),
                name,
                coordinates
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.CARTESIAN_POINT, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}