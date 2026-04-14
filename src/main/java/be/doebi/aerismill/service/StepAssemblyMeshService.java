package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssemblyResult;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.tessellation.shell.DebugSurfaceFamilyMeshes;

public interface StepAssemblyMeshService {
    Mesh generateMesh(AssemblyResult assemblyResult);
    DebugSurfaceFamilyMeshes generateDebugSurfaceFamilyMeshes(AssemblyResult assemblyResult);
}