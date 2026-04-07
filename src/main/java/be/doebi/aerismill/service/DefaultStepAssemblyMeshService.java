package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.ui.AppConsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;


public class DefaultStepAssemblyMeshService implements StepAssemblyMeshService {


    private final Consumer<String> logSink;
    private final AssembledSolidMeshService assembledSolidMeshService;

    public DefaultStepAssemblyMeshService(
            AssembledSolidMeshService assembledSolidMeshService,
            Consumer<String> logSink
    ) {
        this.assembledSolidMeshService = Objects.requireNonNull(
                assembledSolidMeshService,
                "assembledSolidMeshService must not be null"
        );
        this.logSink = Objects.requireNonNull(logSink, "logSink must not be null");
    }

    public DefaultStepAssemblyMeshService(AssembledSolidMeshService assembledSolidMeshService) {
        this(assembledSolidMeshService, message -> {});
    }

    @Override
    public Mesh generateMesh(AssemblyResult assemblyResult) {
        Objects.requireNonNull(assemblyResult, "assemblyResult must not be null");

        List<SolidAssemblyResult> solids = assemblyResult.solids();

        if (solids.isEmpty()) {
            throw new IllegalArgumentException("AssemblyResult contains no solids");
        }

        List<String> failureReasons = new ArrayList<>();
        Optional<PreviewCandidate> bestCandidate = selectBestPreviewableSolid(solids, failureReasons);

        if (bestCandidate.isPresent()) {
            PreviewCandidate selected = bestCandidate.get();
            AppConsole.log("Preview selected solid[" + selected.solidIndex() + "] "
                    + selected.stepId()
                    + " because it has the highest preview score: "
                    + selected.triangleCount() + " triangles, "
                    + selected.vertexCount() + " vertices.");
            return selected.mesh();
        }

        String firstReason = failureReasons.isEmpty()
                ? "No solid produced previewable mesh."
                : failureReasons.getFirst();

        throw new IllegalArgumentException(
                "No previewable solids found in assembly. First reason: " + firstReason
        );
    }

    private Optional<PreviewCandidate> tryBuildPreviewCandidate(
            SolidAssemblyResult solidAssemblyResult,
            int solidIndex,
            List<String> failureReasons
    ) {
        AssembledSolidResult assembledSolidResult = toAssembledSolidResult(solidAssemblyResult, failureReasons);
        if (assembledSolidResult == null) {
            return Optional.empty();
        }

        try {
            Mesh mesh = assembledSolidMeshService.generateMesh(assembledSolidResult);

            if (mesh == null) {
                String reason = "Solid " + solidAssemblyResult.stepId() + ": mesh service returned null";
                failureReasons.add(reason);
                AppConsole.log(reason);
                return Optional.empty();
            }

            int triangleCount = mesh.triangles() == null ? 0 : mesh.triangles().size();
            int vertexCount = mesh.vertices() == null ? 0 : mesh.vertices().size();

            if (triangleCount <= 0) {
                String reason = "Solid " + solidAssemblyResult.stepId() + ": mesh has no triangles";
                failureReasons.add(reason);
                AppConsole.log(reason);
                return Optional.empty();
            }

            AppConsole.log("Preview candidate solid[" + solidIndex + "] "
                    + solidAssemblyResult.stepId() + ": "
                    + triangleCount + " triangles, "
                    + vertexCount + " vertices.");

            return Optional.of(new PreviewCandidate(
                    solidIndex,
                    solidAssemblyResult.stepId(),
                    mesh,
                    triangleCount,
                    vertexCount
            ));
        } catch (IllegalArgumentException | UnsupportedOperationException ex) {
            String reason = "Solid " + solidAssemblyResult.stepId() + ": " + ex.getMessage();
            failureReasons.add(reason);
            AppConsole.log(reason);
            return Optional.empty();
        }
    }

    private Optional<PreviewCandidate> selectBestPreviewableSolid(
            List<SolidAssemblyResult> solids,
            List<String> failureReasons
    ) {
        PreviewCandidate best = null;

        for (int i = 0; i < solids.size(); i++) {
            SolidAssemblyResult solidAssemblyResult = solids.get(i);
            Optional<PreviewCandidate> candidateOpt =
                    tryBuildPreviewCandidate(solidAssemblyResult, i, failureReasons);

            if (candidateOpt.isEmpty()) {
                continue;
            }

            PreviewCandidate candidate = candidateOpt.get();

            if (best == null || isBetter(candidate, best)) {
                best = candidate;
            }
        }

        return Optional.ofNullable(best);
    }

    private AssembledSolidResult toAssembledSolidResult(
            SolidAssemblyResult solidAssemblyResult,
            List<String> failureReasons
    ) {
        if (solidAssemblyResult.solid() != null) {
            return new AssembledSolidResult(
                    solidAssemblyResult.stepId(),
                    solidAssemblyResult.solid()
            );
        }

        if (solidAssemblyResult.solidWithVoids() != null) {
            return new AssembledSolidResult(
                    solidAssemblyResult.stepId(),
                    solidAssemblyResult.solidWithVoids()
            );
        }

        String reason = "Solid " + solidAssemblyResult.stepId() + ": contains no solid payload";
        failureReasons.add(reason);
        AppConsole.log(reason);
        return null;
    }

    private record PreviewCandidate(
            int solidIndex,
            String stepId,
            Mesh mesh,
            int triangleCount,
            int vertexCount
    ) {}

    private boolean isBetter(PreviewCandidate candidate, PreviewCandidate currentBest) {
        if (candidate.triangleCount() > currentBest.triangleCount()) {
            return true;
        }
        if (candidate.triangleCount() < currentBest.triangleCount()) {
            return false;
        }
        return candidate.vertexCount() > currentBest.vertexCount();
    }

    private void log(String message) {
        logSink.accept(message);
    }
}
