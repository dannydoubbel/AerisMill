package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.geometry.Line;

import java.util.List;
import java.util.Map;


public class LineParser implements EntityParser<Line>  {
    @Override
    public Line parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        CartesianPoint point = resolveCartesianPoint(params.get(1), parsedEntities);
        Vector vector = resolveVector(params.get(2), parsedEntities);

        return new Line(
                entity.getId(),
                entity.getRawParameters(),
                name,
                point,
                vector
        );
    }



    private CartesianPoint resolveCartesianPoint(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return (CartesianPoint) parsedEntities.get(token);
    }

    private Vector resolveVector(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return (Vector) parsedEntities.get(token);
    }
}
