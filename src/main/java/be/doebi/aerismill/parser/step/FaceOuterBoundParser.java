package be.doebi.aerismill.parser.step;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.FaceOuterBound;

import java.util.List;
import java.util.Map;

public class FaceOuterBoundParser implements EntityParser<FaceOuterBound>  {
    @Override
    public FaceOuterBound parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        EdgeLoop bound = resolveEdgeLoop(params.get(1), parsedEntities);
        boolean orientation = parseStepBoolean(params.get(2));

        return new FaceOuterBound(
                entity.getId(),
                entity.getRawParameters(),
                name,
                bound,
                orientation
        );
    }



    private boolean parseStepBoolean(String token) {
        return ".T.".equalsIgnoreCase(token.trim());
    }

    private EdgeLoop resolveEdgeLoop(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$") || token.equals("*")) {
            return null;
        }
        return (EdgeLoop) parsedEntities.get(token);
    }
}
