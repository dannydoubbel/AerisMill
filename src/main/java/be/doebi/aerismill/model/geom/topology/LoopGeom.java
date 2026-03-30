package be.doebi.aerismill.model.geom.topology;

import java.util.List;

public record LoopGeom(
        String stepId,
        List<OrientedEdgeGeom> edges
) {}