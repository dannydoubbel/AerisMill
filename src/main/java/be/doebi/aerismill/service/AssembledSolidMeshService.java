package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.tessellation.shell.DebugSurfaceFamilyMeshes;

public interface AssembledSolidMeshService {
    Mesh generateMesh(AssembledSolidResult assembledSolidResult);
    DebugSurfaceFamilyMeshes generateDebugSurfaceFamilyMeshes(AssembledSolidResult assembledSolidResult);
}
