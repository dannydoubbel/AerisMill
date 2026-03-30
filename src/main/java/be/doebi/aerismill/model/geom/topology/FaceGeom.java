package be.doebi.aerismill.model.geom.topology;

import be.doebi.aerismill.model.geom.surface.Surface3;

import java.util.List;

public record FaceGeom(
        String stepId,
        Surface3 surface,
        List<LoopGeom> bounds,
        boolean sameSense
) {}