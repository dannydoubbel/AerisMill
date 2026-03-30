package be.doebi.aerismill.model.geom.topology;

import java.util.List;

public record ShellGeom(
        String stepId,
        List<FaceGeom> faces
) {}
