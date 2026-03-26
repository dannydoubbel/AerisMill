package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.representation.ShapeRepresentation;
import java.util.List;
import java.util.Map;

public class ShapeRepresentationParser implements EntityParser<ShapeRepresentation> {

      @Override
        public ShapeRepresentation parse(StepEntity entity, List<String> params, Map<String, Object> context) {
            String name = unquote(params.get(0));
            List<String> itemRefs = parseRefList(params.get(1));
            String contextRef = params.get(2);

            return new ShapeRepresentation(
                    entity.getId(),
                    entity.getType(),
                    entity.getRawParameters(),
                    name,
                    itemRefs,
                    contextRef
            );
        }

        private String unquote(String value) {
            if (value == null) {
                return null;
            }
            value = value.trim();
            if (value.startsWith("'") && value.endsWith("'") && value.length() >= 2) {
                return value.substring(1, value.length() - 1);
            }
            return value;
        }

        private List<String> parseRefList(String value) {
            value = value.trim();

            if (value.startsWith("(") && value.endsWith(")")) {
                value = value.substring(1, value.length() - 1);
            }

            if (value.isBlank()) {
                return List.of();
            }

            return java.util.Arrays.stream(value.split(","))
                    .map(String::trim)
                    .toList();
        }
    }

