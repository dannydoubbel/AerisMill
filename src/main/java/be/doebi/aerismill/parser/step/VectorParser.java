package be.doebi.aerismill.parser.step;
import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Vector;

import java.util.List;
import java.util.Map;

public class VectorParser implements EntityParser<Vector>  {
    @Override
    public Vector parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = parseStepString(params.get(0));
        Direction orientation = resolveDirection(params.get(1), parsedEntities);
        double magnitude = Double.parseDouble(params.get(2).trim());

        return new Vector(
                entity.getId(),
                entity.getRawParameters(),
                name,
                orientation,
                magnitude
        );
    }

    private String parseStepString(String token) {
        return token.replace("'", "").trim();
    }

    private Direction resolveDirection(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return (Direction) parsedEntities.get(token);
    }
}
