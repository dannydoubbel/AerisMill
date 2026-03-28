package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.EdgeCurve;

import java.util.List;
import java.util.Map;

public class EdgeCurveParser implements EntityParser<EdgeCurve> {

    @Override
    public EdgeCurve parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String edgeStartRef = parseStepReference(params.get(1));
        String edgeEndRef = parseStepReference(params.get(2));
        String edgeGeometryRef = parseStepReference(params.get(3));
        boolean sameSense = StepParserUtils.parseStepBoolean(params.get(4));

        return new EdgeCurve(
                entity.getId(),
                entity.getRawParameters(),
                name,
                edgeStartRef,
                edgeEndRef,
                edgeGeometryRef,
                sameSense
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.EDGE_CURVE, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }

    private String parseStepReference(String token) {
        if (token == null || token.equals("$") || token.equals("*")) {
            return null;
        }
        return token.trim();
    }
}