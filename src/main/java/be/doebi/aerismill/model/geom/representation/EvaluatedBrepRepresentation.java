package be.doebi.aerismill.model.geom.representation;

import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;

import java.util.List;

public record EvaluatedBrepRepresentation(
        String stepId,
        List<SolidGeom> manifoldSolids,
        List<SolidWithVoidsGeom> solidsWithVoids
) {}