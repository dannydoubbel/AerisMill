package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.ConicalSurface;

import java.util.List;
import java.util.Map;

public class ConicalSurfaceParser implements EntityParser<ConicalSurface> {
    @Override
    public ConicalSurface parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        Axis2Placement3D position = resolveAxis2Placement3D(params.get(1), parsedEntities);
        double radius = Double.parseDouble(params.get(2).trim());
        double semiAngle = Double.parseDouble(params.get(3).trim());

        return new ConicalSurface(
                entity.getId(),
                entity.getRawParameters(),
                name,
                position,
                radius,
                semiAngle
        );
    }



    private Axis2Placement3D resolveAxis2Placement3D(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$") || token.equals("*")) {
            return null;
        }
        return (Axis2Placement3D) parsedEntities.get(token);
    }
}
