package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StepParser {

    private final EntityParserRegistry parserRegistry = new EntityParserRegistry();
    private final ComplexEntityInterpreter complexEntityInterpreter = new ComplexEntityInterpreter();

    public StepModel parse(File file, String rawContent) {


        String dataSection = extractDataSection(rawContent);
        List<String> entityChunks = splitEntities(dataSection);

        StepModel stepModel = parseEntities(entityChunks);
        stepModel.setSourceFile(file);
        stepModel.setFileName(file.getName());
        stepModel.setRawContent(rawContent);

        return stepModel;
    }

    private List<String> splitEntities(String dataSection) {
        List<String> entities = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < dataSection.length(); i++) {
            char c = dataSection.charAt(i);
            current.append(c);

            if (c == ';') {
                String entity = current.toString().trim();
                if (!entity.isEmpty()) {
                    entities.add(entity);
                }
                current.setLength(0);
            }
        }

        return entities;
    }

    private String extractDataSection(String rawContent) {
        int dataStart = rawContent.indexOf("DATA;");
        if (dataStart == -1) {
            throw new IllegalArgumentException("STEP file does not contain DATA section.");
        }

        int dataContentStart = dataStart + "DATA;".length();
        int endSec = rawContent.indexOf("ENDSEC;", dataContentStart);
        if (endSec == -1) {
            throw new IllegalArgumentException("STEP file DATA section is not properly closed with ENDSEC;");
        }

        return rawContent.substring(dataContentStart, endSec).trim();
    }

    private StepModel parseEntities(List<String> entityChunks) {
        StepModel stepModel = new StepModel();

        for (String chunk : entityChunks) {
            StepEntity entity = parseEntity(chunk);
            if (entity != null) {
                stepModel.addEntity(entity);
            }
        }

        complexEntityInterpreter.normalize(stepModel);
        return stepModel;
    }

    private StepEntity parseEntity(String chunk) {
        String trimmed = chunk.trim();

        int equalsIndex = trimmed.indexOf('=');
        if (equalsIndex == -1) {
            throw new IllegalArgumentException("Invalid STEP entity: " + chunk);
        }

        String id = trimmed.substring(0, equalsIndex).trim();
        String rightSide = trimmed.substring(equalsIndex + 1).trim();

        if (rightSide.startsWith("(")) {
            String rawParameters = rightSide;

            if (rawParameters.endsWith(";")) {
                rawParameters = rawParameters.substring(0, rawParameters.length() - 1).trim();
            }

            EntityParser parser = parserRegistry.get(StepEntityType.COMPLEX_ENTITY);
            if (parser != null) {
                try {
                    return parser.parse(id, rawParameters);
                } catch (Exception e) {
                    System.out.println("[COMPLEX PARSE FAILED] " + id + " | " + e.getMessage());
                    return new StepEntity(id, StepEntityType.COMPLEX_ENTITY, rawParameters);
                }
            }

            return new StepEntity(id, StepEntityType.COMPLEX_ENTITY, rawParameters);
        }








        int firstParenIndex = rightSide.indexOf('(');
        if (firstParenIndex == -1) {
            throw new IllegalArgumentException("Invalid STEP entity: " + chunk);
        }

        String typeName = rightSide.substring(0, firstParenIndex).trim();
        StepEntityType type = StepEntityType.fromNameOrNull(typeName);
        if (type == null) {
            System.out.println("[SKIP UNKNOWN TYPE] " + id + " | " + typeName);
            return null;
        }

        String rawParameters = rightSide.substring(firstParenIndex).trim();

        if (rawParameters.endsWith(";")) {
            rawParameters = rawParameters.substring(0, rawParameters.length() - 1).trim();
        }

        EntityParser parser = parserRegistry.get(type);
        if (parser != null) {
            return parser.parse(id, rawParameters);
        }

        return new StepEntity(id, type, rawParameters);
    }
}
