package be.doebi.aerismill.service;

import be.doebi.aerismill.assemble.step.geom.AssembledSolidResult;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.tessellation.solid.SolidTessellator;

public interface AssembledSolidMeshService {
    Mesh generateMesh(AssembledSolidResult assembledSolidResult);
}