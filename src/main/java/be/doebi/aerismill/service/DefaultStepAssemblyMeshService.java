package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;
import be.doebi.aerismill.model.mesh.Mesh;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DefaultStepAssemblyMeshService implements StepAssemblyMeshService {

    private final AssembledSolidMeshService assembledSolidMeshService;

    public DefaultStepAssemblyMeshService(AssembledSolidMeshService assembledSolidMeshService) {
        this.assembledSolidMeshService = Objects.requireNonNull(
                assembledSolidMeshService,
                "assembledSolidMeshService must not be null"
        );
    }

    @Override
    public Mesh generateMesh(AssemblyResult assemblyResult) {
        Objects.requireNonNull(assemblyResult, "assemblyResult must not be null");

        List<SolidAssemblyResult> solids = assemblyResult.solids();

        if (solids.isEmpty()) {
            throw new IllegalArgumentException("AssemblyResult contains no solids");
        }

        List<String> failureReasons = new ArrayList<>();

        for (SolidAssemblyResult solidAssemblyResult : solids) {
            AssembledSolidResult assembledSolidResult;

            if (solidAssemblyResult.solid() != null) {
                assembledSolidResult = new AssembledSolidResult(
                        solidAssemblyResult.stepId(),
                        solidAssemblyResult.solid()
                );
            } else if (solidAssemblyResult.solidWithVoids() != null) {
                assembledSolidResult = new AssembledSolidResult(
                        solidAssemblyResult.stepId(),
                        solidAssemblyResult.solidWithVoids()
                );
            } else {
                failureReasons.add("Solid " + solidAssemblyResult.stepId() + ": contains no solid payload");
                continue;
            }

            try {
                return assembledSolidMeshService.generateMesh(assembledSolidResult);
            } catch (IllegalArgumentException | UnsupportedOperationException ex) {
                failureReasons.add("Solid " + solidAssemblyResult.stepId() + ": " + ex.getMessage());
            }
        }

        String firstReason = failureReasons.isEmpty()
                ? "No solid produced previewable mesh."
                : failureReasons.getFirst();

        throw new IllegalArgumentException(
                "No previewable solids found in assembly. First reason: " + firstReason
        );
    }
}