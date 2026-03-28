package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.ClosedShell;

import java.util.List;
import java.util.Map;

public class ClosedShellParser implements EntityParser<ClosedShell> {

    @Override
    public ClosedShell parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        List<String> faceRefs = StepParserUtils.parseReferenceList(params.get(1));

        return new ClosedShell(
                entity.getId(),
                entity.getRawParameters(),
                name,
                faceRefs
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.CLOSED_SHELL, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}