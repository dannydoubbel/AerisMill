package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.Line;

import java.util.List;
import java.util.Map;

public class LineParser implements EntityParser<Line>  {

    @Override
    public Line parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String pointRef = params.get(1).trim();
        String vectorRef = params.get(2).trim();

        return new Line(
                entity.getId(),
                entity.getRawParameters(),
                name,
                pointRef,
                vectorRef
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.LINE, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}