package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.step.base.StepModel;

import java.util.Objects;

public class DefaultStepModelMeshService implements StepModelMeshService {

    private final StepAssemblyService stepAssemblyService;
    private final StepAssemblyMeshService stepAssemblyMeshService;

    public DefaultStepModelMeshService(
            StepAssemblyService stepAssemblyService,
            StepAssemblyMeshService stepAssemblyMeshService
    ) {
        this.stepAssemblyService = Objects.requireNonNull(
                stepAssemblyService,
                "stepAssemblyService must not be null"
        );
        this.stepAssemblyMeshService = Objects.requireNonNull(
                stepAssemblyMeshService,
                "stepAssemblyMeshService must not be null"
        );
    }

    @Override
    public Mesh generateMesh(StepModel stepModel) {
        Objects.requireNonNull(stepModel, "stepModel must not be null");

        AssemblyResult assemblyResult = stepAssemblyService.assemble(stepModel);
        return stepAssemblyMeshService.generateMesh(assemblyResult);
    }
}