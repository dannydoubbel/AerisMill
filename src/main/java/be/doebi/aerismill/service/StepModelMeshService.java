package be.doebi.aerismill.service;

import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.step.base.StepModel;

public interface StepModelMeshService {
    Mesh generateMesh(StepModel stepModel);
}