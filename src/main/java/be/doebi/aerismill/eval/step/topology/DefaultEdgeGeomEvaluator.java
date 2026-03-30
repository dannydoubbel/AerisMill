package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.curve.CurveEvaluator;
import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.VertexGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Circle;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.VertexPoint;

import java.util.Objects;

public final class DefaultEdgeGeomEvaluator implements EdgeGeomEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final CurveEvaluator curveEvaluator;
    private final VertexGeomEvaluator vertexGeomEvaluator;

    public DefaultEdgeGeomEvaluator(
            StepEvaluationContext context,
            CurveEvaluator curveEvaluator,
            VertexGeomEvaluator vertexGeomEvaluator
    ) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.curveEvaluator = Objects.requireNonNull(curveEvaluator, "curveEvaluator must not be null");
        this.vertexGeomEvaluator = Objects.requireNonNull(vertexGeomEvaluator, "vertexGeomEvaluator must not be null");
    }

    @Override
    public EdgeGeom evaluateEdge(EdgeCurve edgeCurve) {
        Objects.requireNonNull(edgeCurve, "edgeCurve must not be null");

        Object cached = cache.getTopology(edgeCurve.getId());
        if (cached instanceof EdgeGeom edgeGeom) {
            return edgeGeom;
        }

        VertexPoint edgeStart = edgeCurve.getEdgeStart();
        if (edgeStart == null) {
            edgeStart = requireVertexPoint(edgeCurve.getEdgeStartRef(), edgeCurve.getId(), "edgeStart");
        }

        VertexPoint edgeEnd = edgeCurve.getEdgeEnd();
        if (edgeEnd == null) {
            edgeEnd = requireVertexPoint(edgeCurve.getEdgeEndRef(), edgeCurve.getId(), "edgeEnd");
        }

        StepEntity edgeGeometry = edgeCurve.getEdgeGeometry();
        if (edgeGeometry == null) {
            edgeGeometry = requireStepEntity(edgeCurve.getEdgeGeometryRef(), edgeCurve.getId(), "edgeGeometry");
        }

        VertexGeom startGeom = vertexGeomEvaluator.evaluateVertex(edgeStart);
        VertexGeom endGeom = vertexGeomEvaluator.evaluateVertex(edgeEnd);
        Curve3 curve = evaluateCurve(edgeGeometry, edgeCurve.getId());

        EdgeGeom result = new EdgeGeom(
                edgeCurve.getId(),
                curve,
                startGeom,
                endGeom,
                edgeCurve.isSameSense()
        );

        cache.putTopology(edgeCurve.getId(), result);
        return result;
    }

    private Curve3 evaluateCurve(StepEntity edgeGeometry, String ownerId) {
        if (edgeGeometry instanceof Line line) {
            return curveEvaluator.evaluateLine(line);
        }
        if (edgeGeometry instanceof Circle circle) {
            return curveEvaluator.evaluateCircle(circle);
        }

        throw new IllegalStateException(
                "Entity " + ownerId + " has unsupported edge geometry type: " + edgeGeometry.getType()
        );
    }

    private VertexPoint requireVertexPoint(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof VertexPoint vertexPoint)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected VERTEX_POINT for " + role + " ref " + ref
            );
        }
        return vertexPoint;
    }

    private StepEntity requireStepEntity(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (entity == null) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " missing " + role + " ref " + ref
            );
        }
        return entity;
    }
}