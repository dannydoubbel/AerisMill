package be.doebi.aerismill.report.step;

import static org.junit.jupiter.api.Assertions.*;

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
import java.util.*;
import java.util.stream.Collectors;
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

                long advancedFaceObjects = loadedModel.getEntities().stream()
                        .filter(entity -> entity instanceof be.doebi.aerismill.model.step.topology.AdvancedFace)
                        .count();

                long closedShellObjects = loadedModel.getEntities().stream()
                        .filter(entity -> entity instanceof be.doebi.aerismill.model.step.topology.ClosedShell)
                        .count();

                long manifoldSolidBrepObjects = loadedModel.getEntities().stream()
                        .filter(entity -> entity instanceof be.doebi.aerismill.model.step.topology.ManifoldSolidBrep)
                        .count();

                long advancedBrepShapeRepresantionObjects = loadedModel.getEntities().stream()
                        .filter(entity -> entity instanceof be.doebi.aerismill.model.step.representation.AdvancedBrepShapeRepresentation)
                        .count();

                long resolvableObjects = loadedModel.getEntities().stream()
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

                System.out.println("VertexPoint runtime objects: " + vertexPointObjects);
                System.out.println("Resolvable runtime objects: " + resolvableObjects);
                System.out.println("Generic count : "+genericCount);
                System.out.println("ADVANCED_FACE count : "+advancedFaceObjects);
                System.out.println("CLOSED_SHELL count : "+closedShellObjects);
                System.out.println("MANIFOLD_SOLID_BREP count : "+manifoldSolidBrepObjects);
                System.out.println("ADVANCED_BREP_SHAPE_REPRESENTATION count : "+advancedBrepShapeRepresantionObjects);


                genericByType.entrySet().stream()
                        .sorted(Map.Entry.<StepEntityType, Long>comparingByValue().reversed())
                        .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));






                StepResolveReport report = loadedModel.resolveAllWithReport();


                if (loadedModel.getName().toLowerCase().equals("GearDrive.STEP".toLowerCase())) {
                    StepEntity e2939 = loadedModel.getEntity("#2939");
                    StepEntity e3233 = loadedModel.getEntity("#3233");
                    StepEntity e70422 = loadedModel.getEntity("#70422");
                    StepEntity e151632 = loadedModel.getEntity("#151632");

                    System.out.println("#2939 = " + (e2939 == null ? "null" : e2939.getRawParameters() + " | " + e2939.getClass().getName() + " | " + e2939.getType()));
                    System.out.println("#3233 = " + (e3233 == null ? "null" : e3233.getRawParameters() + " | " + e3233.getClass().getName() + " | " + e3233.getType()));
                    System.out.println("#70422 = " + (e70422 == null ? "null" : e70422.getRawParameters() + " | " + e70422.getClass().getName() + " | " + e70422.getType()));
                    System.out.println("#151632 = " + (e151632 == null ? "null" : e151632.getRawParameters() + " | " + e151632.getClass().getName() + " | " + e151632.getType()));
                }








                System.out.println("File: " +  loadedModel.getName());

                System.out.println("Resolved OK: " + report.getSuccessCount());
                System.out.println("Resolved FAILED: " + report.getFailureCount());

                for (Map.Entry<StepEntityType, Integer> entry : report.getFailureCountsByType().entrySet()) {
                    System.out.println(entry.getKey() + " -> " + entry.getValue());
                }

                Map<StepEntityType, List<StepResolveFailure>> failuresByType = report.getFailures().stream()
                        .collect(Collectors.groupingBy(StepResolveFailure::entityType));

                for (Map.Entry<StepEntityType, Integer> entry : report.getFailureCountsByType().entrySet()) {
                    System.out.println(entry.getKey() + " -> " + entry.getValue());

                    List<StepResolveFailure> samples = failuresByType.getOrDefault(entry.getKey(), List.of());
                    samples.stream()
                            .limit(5)
                            .forEach(failure -> System.out.println("  " + failure.entityId()
                                    + " | " + failure.exceptionType()
                                    + " | " + failure.message()));
                }



            }
            System.out.println("Scanned files : "+scannedFiles);

        } catch (Exception e) {
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }
}