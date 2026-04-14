package be.doebi.aerismill.tessellation.solid;

import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.tessellation.shell.DebugSurfaceFamilyMeshes;

public interface SolidTessellator {
    Mesh tessellate(SolidGeom solid);
    DebugSurfaceFamilyMeshes tessellateDebugSurfaceFamilies(SolidGeom solid);
}