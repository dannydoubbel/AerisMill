package be.doebi.aerismill.tessellation.shell;

import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.mesh.Mesh;

public interface ShellTessellator {
    Mesh tessellate(ShellGeom shell);
}