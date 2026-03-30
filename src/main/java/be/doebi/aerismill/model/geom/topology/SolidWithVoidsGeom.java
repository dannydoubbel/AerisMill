package be.doebi.aerismill.model.geom.topology;

import java.util.List;

public record SolidWithVoidsGeom(
        String stepId,
        ShellGeom outerShell,
        List<ShellGeom> voidShells
) {}