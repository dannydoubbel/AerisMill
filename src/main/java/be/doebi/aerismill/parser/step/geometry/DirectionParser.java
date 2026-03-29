package be.doebi.aerismill.parser.step.geometry;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DirectionParser implements EntityParser<Direction> {

    @Override
    public Direction parse(
            StepEntity entity,
            List<String> params,
            Map<String, Object> parsedEntities
    ) {
        String name = StepParserUtils.parseStepString(params.get(0));

        String ratiosPart = params.get(1).trim();
        int start = ratiosPart.indexOf('(');
        int end = ratiosPart.lastIndexOf(')');
        String inside = ratiosPart.substring(start + 1, end).trim();

        String[] pieces = inside.split(",");
        List<Double> directionRatios = new ArrayList<>();

        for (String piece : pieces) {
            directionRatios.add(Double.parseDouble(piece.trim()));
        }

        return new Direction(
                entity.getId(),
                entity.getRawParameters(),
                name,
                directionRatios
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.DIRECTION, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}