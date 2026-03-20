package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.topology.ClosedShell;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;

import java.util.List;
import java.util.Map;

public class ManifoldSolidBrepParser implements EntityParser<ManifoldSolidBrep>  {
    @Override
    public ManifoldSolidBrep parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        ClosedShell outer = resolveClosedShell(params.get(1), parsedEntities);

        return new ManifoldSolidBrep(
                entity.getId(),
                entity.getRawParameters(),
                name,
                outer
        );
    }



    private ClosedShell resolveClosedShell(String token, Map<String, Object> parsedEntities) {
        if (token == null || token.equals("$") || token.equals("*")) {
            return null;
        }
        return (ClosedShell) parsedEntities.get(token);
    }
}
