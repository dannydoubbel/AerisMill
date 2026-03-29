package be.doebi.aerismill.parser.step.topology;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class OrientedEdgeParser implements EntityParser<OrientedEdge> {

    @Override
    public OrientedEdge parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String edgeStartRef = parseStepReference(params.get(1));
        String edgeEndRef = parseStepReference(params.get(2));
        String edgeElementRef = parseStepReference(params.get(3));
        boolean orientation = StepParserUtils.parseStepBoolean(params.get(4));

        return new OrientedEdge(
                entity.getId(),
                entity.getRawParameters(),
                name,
                edgeStartRef,
                edgeEndRef,
                edgeElementRef,
                orientation
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.ORIENTED_EDGE, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }

    private String parseStepReference(String token) {
        if (token == null) {
            return null;
        }

        String trimmed = token.trim();
        if (trimmed.equals("$") || trimmed.equals("*")) {
            return null;
        }

        return trimmed;
    }
}