package be.doebi.aerismill.parser.step;
import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.Plane;

import java.util.List;
import java.util.Map;

public class PlaneParser implements EntityParser<Plane>  {
    @Override
    public Plane parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = parseStepString(params.get(0));
        Axis2Placement3D position = resolveAxis2Placement3D(params.get(1), parsedEntities);

        return new Plane(
                entity.getId(),
                entity.getRawParameters(),
                name,
                position
        );
    }

    private String parseStepString(String token) {
        return token.replace("'", "").trim();
    }

    private Axis2Placement3D resolveAxis2Placement3D(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$") || token.equals("*")) {
            return null;
        }
        return (Axis2Placement3D) parsedEntities.get(token);
    }
}
