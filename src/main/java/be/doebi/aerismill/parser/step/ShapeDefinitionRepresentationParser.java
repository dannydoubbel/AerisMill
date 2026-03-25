package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.definition.ShapeDefinitionRepresentation;
import be.doebi.aerismill.parser.step.EntityParser;

import java.util.List;
import java.util.Map;

public class ShapeDefinitionRepresentationParser implements EntityParser<ShapeDefinitionRepresentation> {

    @Override
    public ShapeDefinitionRepresentation parse(StepEntity entity, List<String> params, Map<String, Object> context) {
        String definitionRef = params.get(0);
        String representationRef = params.get(1);

        return new ShapeDefinitionRepresentation(
                entity.getId(),
                entity.getType(),
                entity.getRawParameters(),
                definitionRef,
                representationRef
        );
    }
}