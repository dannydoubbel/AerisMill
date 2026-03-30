package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.model.step.topology.OrientedEdge;

public interface OrientedEdgeGeomEvaluator {
    OrientedEdgeGeom evaluateOrientedEdge(OrientedEdge orientedEdge);
}