package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;

import java.util.List;
import java.util.Map;

public class Axis2Placement3DParser {



    public Axis2Placement3D parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = parseStepString(params.get(0));
        CartesianPoint location = resolveCartesianPoint(params.get(1), parsedEntities);
        Direction axis = params.size() > 2 ? resolveDirection(params.get(2), parsedEntities) : null;
        Direction refDirection = params.size() > 3 ? resolveDirection(params.get(3), parsedEntities) : null;

        return new Axis2Placement3D(
                entity.getId(),
                entity.getRawParameters(),
                name,
                location,
                axis,
                refDirection
        );    }

    private String parseStepString(String token) {
        return token.replace("'", "").trim();
    }

    private CartesianPoint resolveCartesianPoint(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return (CartesianPoint) parsedEntities.get(token);
    }

    private Direction resolveDirection(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return (Direction) parsedEntities.get(token);
    }
}
