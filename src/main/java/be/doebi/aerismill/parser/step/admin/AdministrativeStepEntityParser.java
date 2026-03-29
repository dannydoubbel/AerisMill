package be.doebi.aerismill.parser.step.admin;

import be.doebi.aerismill.model.step.admin.AdministrativeStepEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.parser.step.EntityParser;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AdministrativeStepEntityParser<T extends AdministrativeStepEntity> implements EntityParser<T> {

    private final StepEntityType entityType;
    private final Function<StepEntity, T> factory;

    public AdministrativeStepEntityParser(StepEntityType entityType, Function<StepEntity, T> factory) {
        this.entityType = entityType;
        this.factory = factory;
    }

    @Override
    public T parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        return factory.apply(entity);
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, entityType, rawParameters);
        return parse(entity, List.of(), Map.of());
    }
}