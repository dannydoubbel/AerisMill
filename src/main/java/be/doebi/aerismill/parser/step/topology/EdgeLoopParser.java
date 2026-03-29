package be.doebi.aerismill.parser.step.topology;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class EdgeLoopParser implements EntityParser<EdgeLoop> {

    @Override
    public EdgeLoop parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        List<String> edgeRefs = StepParserUtils.parseReferenceList(params.get(1));

        return new EdgeLoop(
                entity.getId(),
                entity.getRawParameters(),
                name,
                edgeRefs
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.EDGE_LOOP, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}