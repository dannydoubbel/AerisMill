package be.doebi.aerismill.parser.step;
import be.doebi.aerismill.model.step.StepEntity;

import java.util.List;
import java.util.Map;

public interface EntityParser <T> {
    T parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities);
}
