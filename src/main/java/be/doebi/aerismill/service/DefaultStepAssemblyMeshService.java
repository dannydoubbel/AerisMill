package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.assemble.step.geom.SolidAssemblyResult;
import be.doebi.aerismill.model.mesh.Mesh;

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

        if (solids.size() > 1) {
            throw new UnsupportedOperationException("Multiple assembled solids are not supported yet");
        }

        SolidAssemblyResult solidAssemblyResult = solids.getFirst();

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
            throw new IllegalArgumentException("SolidAssemblyResult contains no solid payload");
        }

        return assembledSolidMeshService.generateMesh(assembledSolidResult);
    }
}