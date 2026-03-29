package be.doebi.aerismill.parser.step.geometry;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class Axis2Placement3DParser implements EntityParser<Axis2Placement3D> {

    @Override
    public Axis2Placement3D parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String locationRef = params.get(1).trim();
        String axisRef = params.size() > 2 ? params.get(2).trim() : null;
        String refDirectionRef = params.size() > 3 ? params.get(3).trim() : null;

        return new Axis2Placement3D(
                entity.getId(),
                entity.getRawParameters(),
                name,
                locationRef,
                axisRef,
                refDirectionRef
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.AXIS2_PLACEMENT_3D, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}