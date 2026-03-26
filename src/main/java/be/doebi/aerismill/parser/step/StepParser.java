package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.StepModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StepParser {
        public StepModel parse(File file, String rawContent) {
            System.out.println("Inside STEP PARSER");
            System.out.println("Parsing STEP file: " + file.getAbsolutePath());

            String dataSection = extractDataSection(rawContent);
            List<String> entityChunks = splitEntities(dataSection);
            List<StepEntity> entities = parseEntities(entityChunks);

            return new StepModel(file, file.getName(), rawContent, entities);
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

        private List<StepEntity> parseEntities(List<String> entityChunks) {
            List<StepEntity> entities = new ArrayList<>();

            for (String chunk : entityChunks) {
                entities.add(parseEntity(chunk));
            }

            return entities;
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
                return new StepEntity(id, "COMPLEX_ENTITY", rightSide);
            }

            int firstParenIndex = rightSide.indexOf('(');
            if (firstParenIndex == -1) {
                throw new IllegalArgumentException("Invalid STEP entity: " + chunk);
            }

            String type = rightSide.substring(0, firstParenIndex).trim();
            String rawParameters = rightSide.substring(firstParenIndex).trim();

            if (rawParameters.endsWith(";")) {
                rawParameters = rawParameters.substring(0, rawParameters.length() - 1).trim();
            }

            return new StepEntity(id, type, rawParameters);
        }




}
