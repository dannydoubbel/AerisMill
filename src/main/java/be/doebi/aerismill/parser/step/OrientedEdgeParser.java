package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.OrientedEdge;

import java.util.List;
import java.util.Map;

public class OrientedEdgeParser implements EntityParser<OrientedEdge> {

    @Override
    public OrientedEdge parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String edgeStartRef = parseStepReference(params.get(1));
        String edgeEndRef = parseStepReference(params.get(2));
        String edgeElementRef = parseStepReference(params.get(3));
        boolean orientation = parseStepBoolean(params.get(4));

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

    private boolean parseStepBoolean(String token) {
        return ".T.".equalsIgnoreCase(token.trim());
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