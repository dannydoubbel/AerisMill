package be.doebi.aerismill;

import be.doebi.aerismill.io.FileExtensionHelper;
import be.doebi.aerismill.io.ResourcePathHelper;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.parser.step.EntityParser;
import be.doebi.aerismill.parser.step.EntityParserRegistry;
import be.doebi.aerismill.parser.step.StepParserUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class StepEntityInventoryTest {
    private static final Pattern STEP_ENTITY_PATTERN =
            Pattern.compile("^\\s*(#\\d+)\\s*=\\s*([A-Z0-9_]+)\\s*\\((.*)\\)\\s*;\\s*$", Pattern.CASE_INSENSITIVE);

    private static final Pattern NORMAL_ENTITY_PATTERN =
            Pattern.compile("^\\s*#\\d+\\s*=\\s*([A-Z0-9_]+)\\s*\\(", Pattern.CASE_INSENSITIVE);

    private final Scanner pauseScanner = new Scanner(System.in);

    private final Map<StepEntityType, Integer> parseFailsByType = new EnumMap<>(StepEntityType.class);
    private final Map<String, Map<StepEntityType, Integer>> countsByFileThenType = new TreeMap<>();

    @Test
    void inventoryStepEntities() throws IOException, URISyntaxException {

        Path stepFolder =     ResourcePathHelper.getResourceFolderPath("step");

        Map<StepEntityType, Integer> totalCounts = new EnumMap<>(StepEntityType.class);
        Map<StepEntityType, Set<String>> filesPerType = new EnumMap<>(StepEntityType.class);

        int scannedFiles = 0;

        try (Stream<Path> paths = Files.list(stepFolder)) {
            List<Path> stepFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(FileExtensionHelper::isStepFile)
                    .sorted()
                    .toList();

            for (Path file : stepFiles) {
                scannedFiles++;

                Map<StepEntityType, Integer> fileCounts = scanSingleFile(file);
                ParseStats parseStats = parseSupportedEntities(file);

                System.out.println();
                System.out.println("PARSE SUMMARY");
                System.out.println("--------------------------------------------------");
                System.out.println("Supported entities seen : " + parseStats.supportedSeen);
                System.out.println("Parsed successfully     : " + parseStats.parsedOk);
                System.out.println("Failed to parse         : " + parseStats.parsedFailed);
                System.out.println("Skipped unsupported     : " + parseStats.skippedUnsupported);

                if (true) {
                    System.out.println();
                    System.out.println("Pausing " + 6000 + " ms before next file...");
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Pause interrupted", e);
                    }
                }

                System.out.println("\n==================================================");
                System.out.println("FILE: " + file.getFileName());
                System.out.println("==================================================");

                for (Map.Entry<StepEntityType, Integer> entry : fileCounts.entrySet()) {
                    System.out.printf("%-30s %8d%n", entry.getKey(), entry.getValue());

                    totalCounts.merge(entry.getKey(), entry.getValue(), Integer::sum);
                    filesPerType
                            .computeIfAbsent(entry.getKey(), k -> new TreeSet<>())
                            .add(file.getFileName().toString());
                }

                countsByFileThenType.put(file.getFileName().toString(), fileCounts);

                System.out.println();
                System.out.println("FAILURES BY TYPE");
                System.out.println("--------------------------------------------------");
                for (Map.Entry<StepEntityType, Integer> entry : parseFailsByType.entrySet()) {
                    System.out.printf("%-45s %8d%n", entry.getKey(), entry.getValue());
                }
            }
        }

        System.out.println("\n\n##################################################");
        System.out.println("GLOBAL SUMMARY");
        System.out.println("##################################################");
        System.out.println("Scanned STEP files: " + scannedFiles);
        System.out.println();

        List<StepEntityType> sortedTypes = totalCounts.entrySet().stream()
                .sorted(Map.Entry.<StepEntityType, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        System.out.printf("%-30s %12s %12s%n", "Entity Type", "Total Count", "Files In");
        System.out.println("--------------------------------------------------------------");

        for (StepEntityType type : sortedTypes) {
            int total = totalCounts.get(type);
            int filesIn = filesPerType.getOrDefault(type, Collections.emptySet()).size();
            System.out.printf("%-30s %12d %12d%n", type, total, filesIn);
        }

        System.out.println("\n\n################ HORIZONTAL SUMMARY ################");

        List<String> allFiles = new ArrayList<>(countsByFileThenType.keySet());

        Set<StepEntityType> allTypesSet = new TreeSet<>();
        for (Map<StepEntityType, Integer> fileMap : countsByFileThenType.values()) {
            allTypesSet.addAll(fileMap.keySet());
        }
        List<StepEntityType> allTypes = new ArrayList<>(allTypesSet);

        System.out.printf("%-45s", "Entity Type");
        for (String fileName : allFiles) {
            System.out.printf("%15s", shorten(fileName, 15));
        }
        System.out.printf("%15s%18s%n", "Total", "Files Present");
        System.out.println("-".repeat(45 + (allFiles.size() * 15) + 33));

        for (StepEntityType type : allTypes) {
            System.out.printf("%-45s", type);

            int total = 0;
            int filesPresent = 0;

            for (String fileName : allFiles) {
                int count = countsByFileThenType.get(fileName).getOrDefault(type, 0);
                System.out.printf("%15d", count);
                total += count;
                if (count > 0) {
                    filesPresent++;
                }
            }

            System.out.printf("%15d%18d%n", total, filesPresent);
        }
    }

    private Map<StepEntityType, Integer> scanSingleFile(Path file) throws IOException {
        Map<StepEntityType, Integer> counts = new EnumMap<>(StepEntityType.class);

        String content = readStepFile(file);
        StringBuilder currentEntity = new StringBuilder();

        for (String line : content.split("\\R")) {
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            currentEntity.append(trimmed).append(' ');

            if (trimmed.endsWith(";")) {
                String entityText = currentEntity.toString().trim();
                currentEntity.setLength(0);

                StepEntityType type = extractEntityType(entityText);
                if (type != null) {
                    counts.merge(type, 1, Integer::sum);
                }
            }
        }

        return counts;
    }

    private String readStepFile(Path file) throws IOException {
        List<java.nio.charset.Charset> charsets = List.of(
                StandardCharsets.UTF_8,
                StandardCharsets.ISO_8859_1,
                java.nio.charset.Charset.forName("windows-1252")
        );

        for (java.nio.charset.Charset charset : charsets) {
            try {
                return Files.readString(file, charset);
            } catch (java.nio.charset.MalformedInputException ignored) {
                // try next charset
            }
        }

        throw new IOException("Could not read STEP file with supported encodings: " + file);
    }

    private StepEntityType extractEntityType(String entityText) {
        Matcher normalMatcher = NORMAL_ENTITY_PATTERN.matcher(entityText);
        if (normalMatcher.find()) {
            return StepEntityType.fromNameOrNull(normalMatcher.group(1).trim().toUpperCase());
        }

        if (entityText.matches("^\\s*#\\d+\\s*=\\s*\\(.*")) {
            return StepEntityType.COMPLEX_ENTITY;
        }

        return null;
    }





    private String shorten(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }

    private StepEntity toStepEntity(String entityText) {
        Matcher matcher = STEP_ENTITY_PATTERN.matcher(entityText);
        if (!matcher.matches()) {
            return null;
        }

        String id = matcher.group(1).trim();
        String typeName = matcher.group(2).trim().toUpperCase();
        StepEntityType type = StepEntityType.fromNameOrNull(typeName);
        if (type == null) {
            return null;
        }

        String rawParameters = "(" + matcher.group(3).trim() + ")";
        return new StepEntity(id, type, rawParameters);
    }

    private String stripOuterParens(String rawParameters) {
        String value = rawParameters.trim();
        if (value.startsWith("(") && value.endsWith(")")) {
            return value.substring(1, value.length() - 1).trim();
        }
        return value;
    }

    private ParseStats parseSupportedEntities(Path file) throws IOException {
        ParseStats stats = new ParseStats();

        String content = readStepFile(file);
        StringBuilder currentEntity = new StringBuilder();

        EntityParserRegistry registry = new EntityParserRegistry();

        for (String line : content.split("\\R")) {
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            currentEntity.append(trimmed).append(' ');

            if (trimmed.endsWith(";")) {
                String entityText = currentEntity.toString().trim();
                currentEntity.setLength(0);

                StepEntity entity = toStepEntity(entityText);
                if (entity == null) {
                    continue;
                }

                System.out.println("RAW PARAMETERS = [" + entity.getRawParameters() + "]");
                List<String> paramsList = StepParserUtils.splitTopLevelParameters(entity.getRawParameters());
                System.out.println("PARAMS = " + paramsList);

                StepEntityType entityType = entity.getType();
                System.out.println("[Known type] " + entityType);

                EntityParser<?> parser = registry.get(entityType);
                if (parser == null) {
                    stats.skippedUnsupported++;
                    continue;
                }

                stats.supportedSeen++;

                try {
                    List<String> params = StepParserUtils.splitTopLevelParameters(entity.getRawParameters());

                    Object parsed = parser.parse(entity, params, Map.of());

                    if (parsed == null) {
                        stats.parsedFailed++;
                        parseFailsByType.merge(entityType, 1, Integer::sum);
                        System.out.println("[PARSE FAIL] " + file.getFileName()
                                + " | " + entity.getId()
                                + " | " + entityType
                                + " | parser returned null");
                    } else {
                        stats.parsedOk++;
                    }

                } catch (Exception e) {
                    stats.parsedFailed++;
                    parseFailsByType.merge(entityType, 1, Integer::sum);
                    System.out.println("[PARSE FAIL] " + file.getFileName()
                            + " | " + entity.getId()
                            + " | " + entityType
                            + " | " + e.getClass().getSimpleName()
                            + ": " + e.getMessage());
                }
            }
        }

        return stats;
    }

    private static class ParseStats {
        int supportedSeen;
        int parsedOk;
        int parsedFailed;
        int skippedUnsupported;
    }
}