package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.presentation.PresentationStepEntity;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PresentationStepEntityParser<T extends PresentationStepEntity> implements EntityParser<T> {

    private final Function<StepEntity, T> factory;

    public PresentationStepEntityParser(Function<StepEntity, T> factory) {
        this.factory = factory;
    }

    @Override
    public T parse(StepEntity entity, List<String> params, Map<String, Object> parsedEntities) {
        return factory.apply(entity);
    }
}