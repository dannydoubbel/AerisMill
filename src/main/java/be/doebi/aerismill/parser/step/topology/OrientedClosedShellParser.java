package be.doebi.aerismill.parser.step.topology;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.topology.OrientedClosedShell;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.StepParserUtils;

import java.util.List;
import java.util.Map;

public class OrientedClosedShellParser implements EntityParser<OrientedClosedShell> {

    @Override
    public OrientedClosedShell parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        String name = StepParserUtils.parseStepString(params.get(0));
        String closedShellRef = params.get(2).trim();
        boolean orientation = StepParserUtils.parseStepBoolean(params.get(3));

        return new OrientedClosedShell(
                entity.getId(),
                entity.getRawParameters(),
                name,
                closedShellRef,
                orientation
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.ORIENTED_CLOSED_SHELL, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}