package be.doebi.aerismill.tessellation.shell;

import be.doebi.aerismill.model.mesh.Mesh;

public record DebugSurfaceFamilyMeshes(
        Mesh planarMesh,
        Mesh cylindricalMesh,
        Mesh conicalMesh,
        Mesh toroidalMesh,
        Mesh bSplineMesh,
        int totalFaces,
        int succeededFaces,
        int failedFaces,
        int planarFaceCount,
        int cylindricalFaceCount,
        int conicalFaceCount,
        int toroidalFaceCount,
        int bSplineFaceCount,
        int sphericalUnsupported,
        int planarBridgeFail,
        int triangulationFail,
        int cylindricalSelfIntersect,
        int unknownFail
) {
}
