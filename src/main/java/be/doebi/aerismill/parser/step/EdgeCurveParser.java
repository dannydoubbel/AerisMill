package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.VertexPoint;

import java.util.List;
import java.util.Map;

public class EdgeCurveParser implements EntityParser<EdgeCurve>  {
    @Override
    public EdgeCurve parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = parseStepString(params.get(0));
        VertexPoint edgeStart = resolveVertexPoint(params.get(1), parsedEntities);
        VertexPoint edgeEnd = resolveVertexPoint(params.get(2), parsedEntities);
        StepEntity edgeGeometry = resolveStepEntity(params.get(3), parsedEntities);
        boolean sameSense = parseStepBoolean(params.get(4));

        return new EdgeCurve(
                entity.getId(),
                entity.getRawParameters(),
                name,
                edgeStart,
                edgeEnd,
                edgeGeometry,
                sameSense
        );
    }

    private String parseStepString(String token) {
        return token.replace("'", "").trim();
    }

    private boolean parseStepBoolean(String token) {
        return ".T.".equalsIgnoreCase(token.trim());
    }

    private VertexPoint resolveVertexPoint(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return (VertexPoint) parsedEntities.get(token);
    }

    private StepEntity resolveStepEntity(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return (StepEntity) parsedEntities.get(token);
    }
}
