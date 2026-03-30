package be.doebi.aerismill.report.step;

import be.doebi.aerismill.io.FileExtensionHelper;
import be.doebi.aerismill.io.ResourcePathHelper;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.resolve.StepResolveFailure;
import be.doebi.aerismill.service.StepImportService;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

class StepResolveReportTest {

    @Test
    void inventoryStepEntities() {
        Path stepFolder = ResourcePathHelper.getResourceFolderPath("step");
        StepImportService stepImportService = new StepImportService();
        int scannedFiles = 0;

        try (Stream<Path> paths = Files.list(stepFolder)) {
            List<Path> stepFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(FileExtensionHelper::isStepFile)
                    .sorted()
                    .toList();

            System.out.println("Total files: " + stepFiles.size());

            for (Path file : stepFiles) {
                scannedFiles++;
                StepModel loadedModel = stepImportService.open(file.toFile());

                long resolvableCount = loadedModel.getEntities().stream()
                        .filter(entity -> entity instanceof be.doebi.aerismill.model.step.resolve.StepResolvable)
                        .count();

                long genericCount = loadedModel.getEntities().stream()
                        .filter(entity -> entity.getClass().equals(StepEntity.class))
                        .count();

                Map<StepEntityType, Long> genericByType = loadedModel.getEntities().stream()
                        .filter(entity -> entity.getClass().equals(StepEntity.class))
                        .collect(Collectors.groupingBy(
                                StepEntity::getType,
                                Collectors.counting()
                        ));

                StepResolveReport report = loadedModel.resolveAllWithReport();

                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println("File " + scannedFiles + ": " + loadedModel.getName());
                System.out.println("--------------------------------------------------");
                System.out.println("Total entities     : " + loadedModel.getEntities().size());
                System.out.println("Resolvable entities: " + resolvableCount);
                System.out.println("Generic count      : " + genericCount);
                System.out.println("Resolved OK        : " + report.getSuccessCount());
                System.out.println("Resolved FAILED    : " + report.getFailureCount());

                if (!genericByType.isEmpty()) {
                    System.out.println("Generic fallback by type:");
                    genericByType.entrySet().stream()
                            .sorted(Map.Entry.<StepEntityType, Long>comparingByValue().reversed())
                            .forEach(entry -> System.out.println("  " + entry.getKey() + " -> " + entry.getValue()));
                }

                if (!report.getFailures().isEmpty()) {
                    System.out.println("Failures by type:");
                    Map<StepEntityType, List<StepResolveFailure>> failuresByType = report.getFailures().stream()
                            .collect(Collectors.groupingBy(StepResolveFailure::entityType));

                    report.getFailureCountsByType().entrySet().stream()
                            .sorted(Map.Entry.<StepEntityType, Integer>comparingByValue().reversed())
                            .forEach(entry -> {
                                System.out.println("  " + entry.getKey() + " -> " + entry.getValue());

                                List<StepResolveFailure> samples =
                                        failuresByType.getOrDefault(entry.getKey(), List.of());

                                samples.stream()
                                        .limit(5)
                                        .forEach(failure -> System.out.println(
                                                "    " + failure.entityId()
                                                        + " | " + failure.exceptionType()
                                                        + " | " + failure.message()
                                        ));
                            });
                }
            }

            System.out.println();
            System.out.println("Scanned files: " + scannedFiles);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }
}