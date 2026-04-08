package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.model.mesh.Mesh;

public interface AssembledSolidMeshService {
    Mesh generateMesh(AssembledSolidResult assembledSolidResult);
}
