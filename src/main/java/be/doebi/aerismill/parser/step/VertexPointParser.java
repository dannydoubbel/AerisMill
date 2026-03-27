package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.VertexPoint;

import java.util.List;
import java.util.Map;

public class VertexPointParser implements EntityParser<VertexPoint> {

    @Override
    public VertexPoint parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String vertexGeometryRef = parseStepReference(params.get(1));

        return new VertexPoint(
                entity.getId(),
                entity.getRawParameters(),
                name,
                vertexGeometryRef
        );
    }

    private String parseStepReference(String token) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return token.trim();
    }
}