package be.doebi.aerismill.tessellation.solid;

import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.mesh.Mesh;

public interface SolidTessellator {
    Mesh tessellate(SolidGeom solid);
}