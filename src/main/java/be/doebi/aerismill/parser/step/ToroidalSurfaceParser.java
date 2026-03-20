package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.ToroidalSurface;

import java.util.List;
import java.util.Map;

public class ToroidalSurfaceParser implements EntityParser<ToroidalSurface>  {
    @Override
    public ToroidalSurface parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        Axis2Placement3D position = resolveAxis2Placement3D(params.get(1), parsedEntities);
        double majorRadius = Double.parseDouble(params.get(2).trim());
        double minorRadius = Double.parseDouble(params.get(3).trim());

        return new ToroidalSurface(
                entity.getId(),
                entity.getRawParameters(),
                name,
                position,
                majorRadius,
                minorRadius
        );
    }



    private Axis2Placement3D resolveAxis2Placement3D(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$") || token.equals("*")) {
            return null;
        }
        return (Axis2Placement3D) parsedEntities.get(token);
    }
}
