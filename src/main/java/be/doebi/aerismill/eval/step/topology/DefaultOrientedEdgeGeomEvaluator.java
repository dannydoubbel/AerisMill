package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.OrientedEdge;

import java.util.Objects;

public final class DefaultOrientedEdgeGeomEvaluator implements OrientedEdgeGeomEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final EdgeGeomEvaluator edgeGeomEvaluator;

    public DefaultOrientedEdgeGeomEvaluator(
            StepEvaluationContext context,
            EdgeGeomEvaluator edgeGeomEvaluator
    ) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.edgeGeomEvaluator = Objects.requireNonNull(edgeGeomEvaluator, "edgeGeomEvaluator must not be null");
    }

    @Override
    public OrientedEdgeGeom evaluateOrientedEdge(OrientedEdge orientedEdge) {
        Objects.requireNonNull(orientedEdge, "orientedEdge must not be null");

        Object cached = cache.getTopology(orientedEdge.getId());
        if (cached instanceof OrientedEdgeGeom orientedEdgeGeom) {
            return orientedEdgeGeom;
        }

        EdgeCurve edgeElement = orientedEdge.getEdgeElement();
        if (edgeElement == null) {
            edgeElement = requireEdgeCurve(
                    orientedEdge.getEdgeElementRef(),
                    orientedEdge.getId(),
                    "edgeElement"
            );
        }

        EdgeGeom edgeGeom = edgeGeomEvaluator.evaluateEdge(edgeElement);

        OrientedEdgeGeom result = new OrientedEdgeGeom(
                orientedEdge.getId(),
                edgeGeom,
                orientedEdge.isOrientation()
        );

        cache.putTopology(orientedEdge.getId(), result);
        return result;
    }

    private EdgeCurve requireEdgeCurve(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof EdgeCurve edgeCurve)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected EDGE_CURVE for " + role + " ref " + ref
            );
        }
        return edgeCurve;
    }
}