package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.model.mesh.Mesh;

public interface StepAssemblyMeshService {
    Mesh generateMesh(AssemblyResult assemblyResult);
}