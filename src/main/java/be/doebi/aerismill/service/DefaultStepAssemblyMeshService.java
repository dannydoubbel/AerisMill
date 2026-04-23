package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;

import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;
import be.doebi.aerismill.tessellation.shell.DebugSurfaceFamilyMeshes;
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
        List<PreviewCandidate> previewCandidates = collectPreviewCandidates(solids, failureReasons);

        if (previewCandidates.isEmpty()) {
            String firstReason = failureReasons.isEmpty()
                    ? "No solid produced previewable mesh."
                    : failureReasons.getFirst();

            throw new IllegalArgumentException(
                    "No previewable solids found in assembly. First reason: " + firstReason
            );
        }

        Mesh combinedMesh = previewCandidates.getFirst().mesh();

        for (int i = 1; i < previewCandidates.size(); i++) {
            PreviewCandidate candidate = previewCandidates.get(i);
            combinedMesh = appendMesh(combinedMesh, candidate.mesh());
        }

        AppConsole.log(
                "Preview combined " + previewCandidates.size() + " solid(s) into one mesh: "
                        + combinedMesh.triangles().size() + " triangles, "
                        + combinedMesh.vertices().size() + " vertices."
        );

        return combinedMesh;
    }

    private List<PreviewCandidate> collectPreviewCandidates(
            List<SolidAssemblyResult> solids,
            List<String> failureReasons
    ) {
        List<PreviewCandidate> previewCandidates = new ArrayList<>();

        for (int i = 0; i < solids.size(); i++) {
            SolidAssemblyResult solidAssemblyResult = solids.get(i);
            Optional<PreviewCandidate> candidateOpt =
                    tryBuildPreviewCandidate(solidAssemblyResult, i, failureReasons);

            candidateOpt.ifPresent(previewCandidates::add);
        }

        return previewCandidates;
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

    private Mesh appendMesh(Mesh base, Mesh addition) {
        Objects.requireNonNull(base, "base mesh must not be null");
        Objects.requireNonNull(addition, "addition mesh must not be null");

        List<MeshVertex> vertices = new ArrayList<>(
                base.vertices().size() + addition.vertices().size()
        );
        vertices.addAll(base.vertices());

        int vertexOffset = base.vertices().size();
        vertices.addAll(addition.vertices());

        List<MeshTriangle> triangles = new ArrayList<>(
                base.triangles().size() + addition.triangles().size()
        );
        triangles.addAll(base.triangles());

        for (MeshTriangle triangle : addition.triangles()) {
            triangles.add(new MeshTriangle(
                    triangle.a() + vertexOffset,
                    triangle.b() + vertexOffset,
                    triangle.c() + vertexOffset
            ));
        }

        return new Mesh(vertices, triangles);
    }

    private record PreviewCandidate(
            int solidIndex,
            String stepId,
            Mesh mesh,
            int triangleCount,
            int vertexCount
    ) {}





















    private Mesh appendIfNotNull(Mesh base, Mesh addition) {
        if (addition == null || addition.isEmpty()) {
            return base;
        }
        if (base == null || base.isEmpty()) {
            return addition;
        }
        return appendMesh(base, addition);
    }

    @Override
    public DebugSurfaceFamilyMeshes generateDebugSurfaceFamilyMeshes(AssemblyResult assemblyResult) {
        Objects.requireNonNull(assemblyResult, "assemblyResult must not be null");

        List<SolidAssemblyResult> solids = assemblyResult.solids();
        if (solids.isEmpty()) {
            throw new IllegalArgumentException("AssemblyResult contains no solids");
        }

        Mesh planar = null;
        Mesh cylindrical = null;
        Mesh conical = null;
        Mesh toroidal = null;

        int totalFaces = 0;
        int succeededFaces = 0;
        int failedFaces = 0;

        List<String> failureReasons = new ArrayList<>();
        boolean foundAnyPreviewable = false;

        for (int i = 0; i < solids.size(); i++) {
            SolidAssemblyResult solidAssemblyResult = solids.get(i);

            AssembledSolidResult assembledSolid =
                    toAssembledSolidResult(solidAssemblyResult, failureReasons);

            if (assembledSolid == null) {
                continue;
            }

            try {
                DebugSurfaceFamilyMeshes debugMeshes =
                        assembledSolidMeshService.generateDebugSurfaceFamilyMeshes(assembledSolid);

                totalFaces += debugMeshes.totalFaces();
                succeededFaces += debugMeshes.succeededFaces();
                failedFaces += debugMeshes.failedFaces();

                boolean hasPlanar = debugMeshes.planarMesh() != null && !debugMeshes.planarMesh().isEmpty();
                boolean hasCylindrical = debugMeshes.cylindricalMesh() != null && !debugMeshes.cylindricalMesh().isEmpty();
                boolean hasConical = debugMeshes.conicalMesh() != null && !debugMeshes.conicalMesh().isEmpty();
                boolean hasToroidal = debugMeshes.toroidalMesh() != null && !debugMeshes.toroidalMesh().isEmpty();

                if (!hasPlanar && !hasCylindrical && !hasConical && !hasToroidal) {
                    String reason = "Solid " + solidAssemblyResult.stepId() + ": debug mesh has no previewable families";
                    failureReasons.add(reason);
                    AppConsole.log(reason);
                    continue;
                }

                planar = appendIfNotNull(planar, debugMeshes.planarMesh());
                cylindrical = appendIfNotNull(cylindrical, debugMeshes.cylindricalMesh());
                conical = appendIfNotNull(conical, debugMeshes.conicalMesh());
                toroidal = appendIfNotNull(toroidal, debugMeshes.toroidalMesh());

                foundAnyPreviewable = true;

                AppConsole.log(
                        "Debug preview candidate solid[" + i + "] "
                                + solidAssemblyResult.stepId()
                                + ": planar=" + countTriangles(debugMeshes.planarMesh())
                                + ", cylindrical=" + countTriangles(debugMeshes.cylindricalMesh())
                                + ", conical=" + countTriangles(debugMeshes.conicalMesh())
                                + ", toroidal=" + countTriangles(debugMeshes.toroidalMesh())
                );

            } catch (IllegalArgumentException | UnsupportedOperationException ex) {
                String reason = "Solid " + solidAssemblyResult.stepId() + ": " + ex.getMessage();
                failureReasons.add(reason);
                AppConsole.log(reason);
            }
        }

        if (!foundAnyPreviewable) {
            String firstReason = failureReasons.isEmpty()
                    ? "No solid produced previewable debug mesh."
                    : failureReasons.getFirst();

            throw new IllegalArgumentException(
                    "No previewable solids found in debug assembly. First reason: " + firstReason
            );
        }

        return new DebugSurfaceFamilyMeshes(
                planar,
                cylindrical,
                conical,
                toroidal,
                totalFaces,
                succeededFaces,
                failedFaces
        );
    }

    private int countTriangles(Mesh mesh) {
        return mesh == null ? 0 : mesh.triangleCount();
    }





}