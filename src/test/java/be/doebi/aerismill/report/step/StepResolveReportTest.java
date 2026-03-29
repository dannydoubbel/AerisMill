package be.doebi.aerismill.report.step;

import static org.junit.jupiter.api.Assertions.*;

import be.doebi.aerismill.io.FileExtensionHelper;
import be.doebi.aerismill.io.ResourcePathHelper;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.service.StepImportService;
import org.junit.jupiter.api.Test;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

class StepResolveReportTest {
    @Test
    void inventoryStepEntities() {
        Path stepFolder = ResourcePathHelper.getResourceFolderPath("step");


        StepImportService stepModelImportService = new StepImportService();
        int scannedFiles = 0;

        try (Stream<Path> paths = Files.list(stepFolder)) {
            List<Path> stepFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(FileExtensionHelper::isStepFile)
                    .sorted()
                    .toList();

            System.out.println("Total files : " + stepFiles.size());
            for (Path file : stepFiles) {
                scannedFiles++;
                System.out.println("nr "+scannedFiles + " name " + file.toFile());
                StepModel loadedModel = stepModelImportService.open(file.toFile());

                loadedModel.getEntities().stream()
                        .filter(entity -> entity.getType() == StepEntityType.VERTEX_POINT)
                        .limit(10)
                        .forEach(entity -> System.out.println(
                                entity.getId() + " | " +
                                        entity.getType() + " | " +
                                        entity.getClass().getName()
                        ));



                long vertexPointObjects = loadedModel.getEntities().stream()
                        .filter(entity -> entity instanceof be.doebi.aerismill.model.step.topology.VertexPoint)
                        .count();

                long resolvableObjects = loadedModel.getEntities().stream()
                        .filter(entity -> entity instanceof be.doebi.aerismill.model.step.resolve.StepResolvable)
                        .count();

                System.out.println("VertexPoint runtime objects: " + vertexPointObjects);
                System.out.println("Resolvable runtime objects: " + resolvableObjects);






                StepResolveReport report = loadedModel.resolveAllWithReport();

                System.out.println("File: " +  loadedModel.getName());

                System.out.println("Resolved OK: " + report.getSuccessCount());
                System.out.println("Resolved FAILED: " + report.getFailureCount());

                for (Map.Entry<StepEntityType, Integer> entry : report.getFailureCountsByType().entrySet()) {
                    System.out.println(entry.getKey() + " -> " + entry.getValue());
                }


            }
            System.out.println("Scanned files : "+scannedFiles);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }
}