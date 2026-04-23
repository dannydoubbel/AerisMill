package be.doebi.aerismill.tessellation.shell;

import be.doebi.aerismill.model.mesh.Mesh;

public record DebugSurfaceFamilyMeshes(
        Mesh planarMesh,
        Mesh cylindricalMesh,
        Mesh conicalMesh,
        Mesh toroidalMesh,   // <-- add this
        int totalFaces,
        int succeededFaces,
        int failedFaces
) {}
