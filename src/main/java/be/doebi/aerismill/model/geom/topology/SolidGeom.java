package be.doebi.aerismill.model.geom.topology;

public record SolidGeom(
        String stepId,
        ShellGeom outerShell
) {}