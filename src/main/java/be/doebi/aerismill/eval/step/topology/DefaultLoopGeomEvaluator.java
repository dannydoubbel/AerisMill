package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.OrientedEdge;

import java.util.List;
import java.util.Objects;

public final class DefaultLoopGeomEvaluator implements LoopGeomEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator;

    public DefaultLoopGeomEvaluator(
            StepEvaluationContext context,
            OrientedEdgeGeomEvaluator orientedEdgeGeomEvaluator
    ) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.orientedEdgeGeomEvaluator = Objects.requireNonNull(
                orientedEdgeGeomEvaluator,
                "orientedEdgeGeomEvaluator must not be null"
        );
    }

    @Override
    public LoopGeom evaluateLoop(EdgeLoop edgeLoop) {
        Objects.requireNonNull(edgeLoop, "edgeLoop must not be null");

        Object cached = cache.getTopology(edgeLoop.getId());
        if (cached instanceof LoopGeom loopGeom) {
            return loopGeom;
        }

        List<OrientedEdge> orientedEdges = edgeLoop.getEdgeList();
        if (orientedEdges == null) {
            orientedEdges = requireOrientedEdges(edgeLoop);
        }

        List<OrientedEdgeGeom> edgeGeoms = orientedEdges.stream()
                .map(orientedEdgeGeomEvaluator::evaluateOrientedEdge)
                .toList();

        LoopGeom result = new LoopGeom(edgeLoop.getId(), edgeGeoms);
        cache.putTopology(edgeLoop.getId(), result);
        return result;
    }

    private List<OrientedEdge> requireOrientedEdges(EdgeLoop edgeLoop) {
        return edgeLoop.getEdgeRefs().stream()
                .map(ref -> requireOrientedEdge(ref, edgeLoop.getId()))
                .toList();
    }

    private OrientedEdge requireOrientedEdge(String ref, String ownerId) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof OrientedEdge orientedEdge)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected ORIENTED_EDGE ref " + ref
            );
        }
        return orientedEdge;
    }
}