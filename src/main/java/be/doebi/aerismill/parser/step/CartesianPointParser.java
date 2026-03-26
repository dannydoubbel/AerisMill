package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;

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
}