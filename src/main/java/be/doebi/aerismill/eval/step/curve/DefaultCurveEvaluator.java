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
import be.doebi.aerismill.model.geom.curve.EllipseCurve3;
import be.doebi.aerismill.model.step.geometry.Ellipse;
import be.doebi.aerismill.model.geom.curve.BSplineCurve3;
import be.doebi.aerismill.model.step.base.StepLogical;
import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;

import java.util.List;
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

    @Override
    public Curve3 evaluate(Ellipse ellipse) {
        return evaluateEllipse(ellipse);
    }


    @Override
    public Curve3 evaluate(BSplineCurveWithKnots bSplineCurveWithKnots) {
        return evaluateBSplineCurveWithKnots(bSplineCurveWithKnots);
    }

    @Override
    public BSplineCurve3 evaluateBSplineCurveWithKnots(BSplineCurveWithKnots spline) {
        Objects.requireNonNull(spline, "spline must not be null");

        Curve3 cached = cache.getCurve(spline.getId());
        if (cached instanceof BSplineCurve3 bSplineCurve3) {
            return bSplineCurve3;
        }

        List<Point3> controlPoints = spline.getControlPoints() == null
                ? requireControlPoints(spline)
                : spline.getControlPoints().stream()
                  .map(this::asCartesianPoint)
                  .map(placementEvaluator::evaluatePoint)
                  .toList();

        List<Double> expandedKnots = expandKnots(
                spline.getKnotMultiplicities(),
                spline.getKnots(),
                spline.getId()
        );

        BSplineCurve3 result = new BSplineCurve3(
                spline.getDegree(),
                controlPoints,
                expandedKnots,
                spline.isClosedCurve() == StepLogical.TRUE,
                spline.isSelfIntersect() == StepLogical.TRUE,
                spline.getCurveForm(),
                spline.getKnotSpec(),
                spline.getWeights()
        );

        cache.putCurve(spline.getId(), result);
        return result;
    }



    @Override
    public EllipseCurve3 evaluateEllipse(Ellipse ellipse) {
        Objects.requireNonNull(ellipse, "ellipse must not be null");

        Curve3 cached = cache.getCurve(ellipse.getId());
        if (cached instanceof EllipseCurve3 ellipseCurve3) {
            return ellipseCurve3;
        }

        Axis2Placement3D position = ellipse.getPosition();
        if (position == null) {
            position = requireAxis2Placement3D(ellipse.getPositionRef(), ellipse.getId(), "position");
        }

        Frame3 frame = placementEvaluator.evaluateAxis2Placement3D(position);
        EllipseCurve3 result = new EllipseCurve3(frame, ellipse.getSemiAxis1(), ellipse.getSemiAxis2());

        cache.putCurve(ellipse.getId(), result);
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

    private List<Point3> requireControlPoints(BSplineCurveWithKnots spline) {
        return spline.getControlPointRefs().stream()
                .map(ref -> context.getStepModel().getEntity(ref))
                .map(this::asCartesianPoint)
                .map(placementEvaluator::evaluatePoint)
                .toList();
    }

    private CartesianPoint asCartesianPoint(StepEntity entity) {
        if (!(entity instanceof CartesianPoint point)) {
            throw new IllegalStateException(
                    "Expected CARTESIAN_POINT but found " +
                            (entity == null ? "null" : entity.getType())
            );
        }
        return point;
    }

    private List<Double> expandKnots(List<Integer> multiplicities, List<Double> knots, String ownerId) {
        if (multiplicities == null || knots == null) {
            throw new IllegalStateException("Spline " + ownerId + " has null knot data");
        }
        if (multiplicities.size() != knots.size()) {
            throw new IllegalStateException(
                    "Spline " + ownerId + " knot multiplicities size does not match knot values size"
            );
        }

        List<Double> expanded = new java.util.ArrayList<>();
        for (int i = 0; i < knots.size(); i++) {
            int multiplicity = multiplicities.get(i);
            double knot = knots.get(i);

            for (int j = 0; j < multiplicity; j++) {
                expanded.add(knot);
            }
        }
        return List.copyOf(expanded);
    }
}
