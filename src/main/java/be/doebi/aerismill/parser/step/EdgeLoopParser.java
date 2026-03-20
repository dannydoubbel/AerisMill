package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.model.step.topology.EdgeLoop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EdgeLoopParser implements EntityParser<EdgeLoop>  {
    public EdgeLoop parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = parseStepString(params.get(0));
        List<OrientedEdge> edgeList = resolveOrientedEdgeList(params.get(1), parsedEntities);

        return new EdgeLoop(
                entity.getId(),
                entity.getRawParameters(),
                name,
                edgeList
        );
    }

    private String parseStepString(String token) {
        return token.replace("'", "").trim();
    }

    private List<OrientedEdge> resolveOrientedEdgeList(String token, Map<String, Object> parsedEntities) {
        List<OrientedEdge> result = new ArrayList<>();

        if (token == null || token.equals("$")) {
            return result;
        }

        String trimmed = token.trim();

        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            trimmed = trimmed.substring(1, trimmed.length() - 1);
        }

        if (trimmed.isBlank()) {
            return result;
        }

        String[] refs = trimmed.split(",");

        for (String ref : refs) {
            String cleanRef = ref.trim();
            OrientedEdge edge = (OrientedEdge) parsedEntities.get(cleanRef);
            if (edge != null) {
                result.add(edge);
            }
        }

        return result;
    }
}
