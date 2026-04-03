package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.topology.FaceGeom;

public interface FaceTessellator {
    FaceMeshPatch tessellate(FaceGeom face);
}