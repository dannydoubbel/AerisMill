package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;

import java.util.List;
import java.util.Map;

public class ManifoldSolidBrepParser implements EntityParser<ManifoldSolidBrep>  {

    @Override
    public ManifoldSolidBrep parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String outerRef = parseStepReference(params.get(1));

        return new ManifoldSolidBrep(
                entity.getId(),
                entity.getRawParameters(),
                name,
                outerRef
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.MANIFOLD_SOLID_BREP, rawParameters);
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