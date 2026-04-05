package be.doebi.aerismill.tessellation.solid;

import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.tessellation.shell.ShellTessellator;

public class DefaultSolidTessellator implements SolidTessellator {

    private final ShellTessellator shellTessellator;

    public DefaultSolidTessellator(ShellTessellator shellTessellator) {
        if (shellTessellator == null) {
            throw new IllegalArgumentException("Shell tessellator must not be null.");
        }
        this.shellTessellator = shellTessellator;
    }

    @Override
    public Mesh tessellate(SolidGeom solid) {
        if (solid == null) {
            throw new IllegalArgumentException("Solid must not be null.");
        }

        ShellGeom outerShell = solid.outerShell();
        if (outerShell == null) {
            throw new IllegalArgumentException("Solid outer shell must not be null.");
        }

        return shellTessellator.tessellate(outerShell);
    }
}