package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.ComplexEntity;
import be.doebi.aerismill.model.step.ComplexEntityPart;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComplexEntityParser implements EntityParser<ComplexEntity> {

    @Override
    public ComplexEntity parse(
            StepEntity entity,
            List<String> params,
            Map<String, Object> parsedEntities
    ) {
        List<ComplexEntityPart> parts = parseParts(entity.getRawParameters());

        return new ComplexEntity(
                entity.getId(),
                entity.getRawParameters(),
                parts
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.COMPLEX_ENTITY, rawParameters);
        return parse(entity, List.of(), Map.of());
    }

    private List<ComplexEntityPart> parseParts(String rawParameters) {
        String trimmed = rawParameters.trim();

        if (!trimmed.startsWith("(") || !trimmed.endsWith(")")) {
            throw new IllegalArgumentException("Invalid COMPLEX_ENTITY raw parameters: " + rawParameters);
        }

        String inside = trimmed.substring(1, trimmed.length() - 1).trim();
        List<String> rawParts = extractComplexEntityParts(inside);

        List<ComplexEntityPart> result = new ArrayList<>();
        for (String rawPart : rawParts) {
            result.add(parsePart(rawPart));
        }

        return result;
    }

    private ComplexEntityPart parsePart(String rawPart) {
        String trimmed = rawPart.trim();
        int parenIndex = trimmed.indexOf('(');

        if (parenIndex < 0 || !trimmed.endsWith(")")) {
            throw new IllegalArgumentException("Invalid COMPLEX_ENTITY part: " + rawPart);
        }

        String type = trimmed.substring(0, parenIndex).trim();
        String paramBlock = trimmed.substring(parenIndex).trim();

        List<String> params = StepParserUtils.splitTopLevelParameters(paramBlock);

        return new ComplexEntityPart(type, params);
    }

    private List<String> extractComplexEntityParts(String text) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;
        boolean insidePart = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (!insidePart && !Character.isWhitespace(c)) {
                insidePart = true;
            }

            current.append(c);

            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0 && insidePart) {
                    parts.add(current.toString().trim());
                    current.setLength(0);
                    insidePart = false;
                }
            }
        }

        if (!current.toString().trim().isEmpty()) {
            throw new IllegalArgumentException("Unbalanced COMPLEX_ENTITY content: " + text);
        }

        return parts;
    }
}