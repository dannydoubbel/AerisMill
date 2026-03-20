package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartesianPointParser implements EntityParser<CartesianPoint>  {

    @Override
    public CartesianPoint parse(
            StepEntity entity,
            List<String> params,
            Map<String, Object> parsedEntities
    ) {
        return CartesianPointParser.parse(entity.getId(), entity.getRawParameters());
    }

    public static CartesianPoint parse(String id, String rawParameters) {
        String trimmed = rawParameters.trim();

        int firstComma = trimmed.indexOf(",");
        String namePart = trimmed.substring(0, firstComma).trim();
        String coordsPart = trimmed.substring(firstComma + 1).trim();

        String name = StepParserUtils.parseStepString(namePart);

        int start = coordsPart.indexOf('(');
        int end = coordsPart.lastIndexOf(')');
        String inside = coordsPart.substring(start + 1, end).trim();

        String[] pieces = inside.split(",");
        List<Double> coordinates = new ArrayList<>();

        for (String piece : pieces) {
            coordinates.add(Double.parseDouble(piece.trim()));
        }

        return new CartesianPoint(id, rawParameters, name, coordinates);
    }


}
