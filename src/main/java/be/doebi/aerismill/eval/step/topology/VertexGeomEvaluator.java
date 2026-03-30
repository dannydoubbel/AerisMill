package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.VertexGeom;
import be.doebi.aerismill.model.step.topology.VertexPoint;

public interface VertexGeomEvaluator {
    VertexGeom evaluateVertex(VertexPoint vertexPoint);
}