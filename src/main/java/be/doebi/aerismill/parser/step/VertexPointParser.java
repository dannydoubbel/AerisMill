package be.doebi.aerismill.parser.step;
import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.topology.VertexPoint;

import java.util.List;
import java.util.Map;

public class VertexPointParser implements EntityParser<VertexPoint>  {
    @Override
    public VertexPoint parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        CartesianPoint vertexGeometry = resolveCartesianPoint(params.get(1), parsedEntities);

        return new VertexPoint(
                entity.getId(),
                entity.getRawParameters(),
                name,
                vertexGeometry
        );
    }

    String name = StepParserUtils.parseStepString(params.get(0));

    private CartesianPoint resolveCartesianPoint(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$")) {
            return null;
        }
        return (CartesianPoint) parsedEntities.get(token);
    }
}
