package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.step.topology.BrepWithVoids;

public interface SolidWithVoidsGeomEvaluator {
    SolidWithVoidsGeom evaluateSolid(BrepWithVoids brepWithVoids);
}