package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.VertexGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.topology.VertexPoint;

import java.util.Objects;

public final class DefaultVertexGeomEvaluator implements VertexGeomEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final PlacementEvaluator placementEvaluator;

    public DefaultVertexGeomEvaluator(StepEvaluationContext context, PlacementEvaluator placementEvaluator) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.placementEvaluator = Objects.requireNonNull(placementEvaluator, "placementEvaluator must not be null");
    }

    @Override
    public VertexGeom evaluateVertex(VertexPoint vertexPoint) {
        Objects.requireNonNull(vertexPoint, "vertexPoint must not be null");

        Object cached = cache.getTopology(vertexPoint.getId());
        if (cached instanceof VertexGeom vertexGeom) {
            return vertexGeom;
        }

        CartesianPoint vertexGeometry = vertexPoint.getVertexGeometry();
        if (vertexGeometry == null) {
            vertexGeometry = requireCartesianPoint(
                    vertexPoint.getVertexGeometryRef(),
                    vertexPoint.getId(),
                    "vertexGeometry"
            );
        }

        Point3 position = placementEvaluator.evaluatePoint(vertexGeometry);
        VertexGeom result = new VertexGeom(vertexPoint.getId(), position);

        cache.putTopology(vertexPoint.getId(), result);
        return result;
    }

    private CartesianPoint requireCartesianPoint(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof CartesianPoint point)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected CARTESIAN_POINT for " + role + " ref " + ref
            );
        }
        return point;
    }
}