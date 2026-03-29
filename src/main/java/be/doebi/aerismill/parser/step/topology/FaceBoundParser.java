package be.doebi.aerismill.parser.step.topology;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.FaceBound;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;


import java.util.List;
import java.util.Map;

public class FaceBoundParser implements EntityParser<FaceBound> {

    @Override
    public FaceBound parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String boundRef = params.get(1).trim();
        boolean orientation = StepParserUtils.parseStepBoolean(params.get(2));

        return new FaceBound(
                entity.getId(),
                entity.getRawParameters(),
                name,
                boundRef,
                orientation
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.FACE_BOUND, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}