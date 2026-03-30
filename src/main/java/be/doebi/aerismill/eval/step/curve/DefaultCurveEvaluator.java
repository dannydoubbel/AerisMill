package be.doebi.aerismill.eval.step.curve;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.curve.CircleCurve3;
import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.Circle;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;

import java.util.Objects;

public final class DefaultCurveEvaluator implements CurveEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final PlacementEvaluator placementEvaluator;

    public DefaultCurveEvaluator(StepEvaluationContext context, PlacementEvaluator placementEvaluator) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.placementEvaluator = Objects.requireNonNull(placementEvaluator, "placementEvaluator must not be null");
    }

    @Override
    public Curve3 evaluate(Line line) {
        return evaluateLine(line);
    }

    @Override
    public Curve3 evaluate(Circle circle) {
        return evaluateCircle(circle);
    }

    @Override
    public LineCurve3 evaluateLine(Line line) {
        Objects.requireNonNull(line, "line must not be null");

        Curve3 cached = cache.getCurve(line.getId());
        if (cached instanceof LineCurve3 lineCurve3) {
            return lineCurve3;
        }

        CartesianPoint point = line.getPoint();
        if (point == null) {
            point = requireCartesianPoint(line.getPointRef(), line.getId(), "point");
        }

        Vector vector = line.getVector();
        if (vector == null) {
            vector = requireVector(line.getVectorRef(), line.getId(), "vector");
        }

        Point3 origin = placementEvaluator.evaluatePoint(point);
        Vec3 directionVector = placementEvaluator.evaluateVector(vector);
        UnitVec3 direction = UnitVec3.of(directionVector);

        LineCurve3 result = new LineCurve3(origin, direction);
        cache.putCurve(line.getId(), result);
        return result;
    }

    @Override
    public CircleCurve3 evaluateCircle(Circle circle) {
        Objects.requireNonNull(circle, "circle must not be null");

        Curve3 cached = cache.getCurve(circle.getId());
        if (cached instanceof CircleCurve3 circleCurve3) {
            return circleCurve3;
        }

        Axis2Placement3D position = circle.getPosition();
        if (position == null) {
            position = requireAxis2Placement3D(circle.getPositionRef(), circle.getId(), "position");
        }

        Frame3 frame = placementEvaluator.evaluateAxis2Placement3D(position);
        CircleCurve3 result = new CircleCurve3(frame, circle.getRadius());

        cache.putCurve(circle.getId(), result);
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

    private Vector requireVector(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof Vector vector)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected VECTOR for " + role + " ref " + ref
            );
        }
        return vector;
    }

    private Axis2Placement3D requireAxis2Placement3D(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof Axis2Placement3D placement)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected AXIS2_PLACEMENT_3D for " + role + " ref " + ref
            );
        }
        return placement;
    }
}