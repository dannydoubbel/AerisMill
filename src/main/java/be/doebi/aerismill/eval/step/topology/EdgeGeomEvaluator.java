package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.step.topology.EdgeCurve;

public interface EdgeGeomEvaluator {
    EdgeGeom evaluateEdge(EdgeCurve edgeCurve);
}