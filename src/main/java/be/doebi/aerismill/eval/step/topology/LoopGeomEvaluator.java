package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.step.topology.EdgeLoop;

public interface LoopGeomEvaluator {
    LoopGeom evaluateLoop(EdgeLoop edgeLoop);
}