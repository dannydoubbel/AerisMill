package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.Direction;

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


    public static Direction parse(String id, String rawParameters) {
        String trimmed = rawParameters.trim();

        int firstComma = trimmed.indexOf(",");
        String namePart = trimmed.substring(0, firstComma).trim();
        String ratiosPart = trimmed.substring(firstComma + 1).trim();

        String name = StepParserUtils.parseStepString(namePart);

        int start = ratiosPart.indexOf('(');
        int end = ratiosPart.lastIndexOf(')');
        String inside = ratiosPart.substring(start + 1, end).trim();

        String[] pieces = inside.split(",");
        List<Double> directionRatios = new ArrayList<>();

        for (String piece : pieces) {
            directionRatios.add(Double.parseDouble(piece.trim()));
        }

        return new Direction(id, rawParameters, name, directionRatios);
    }


}
