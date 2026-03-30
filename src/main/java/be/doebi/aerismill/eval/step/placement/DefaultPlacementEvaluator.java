package be.doebi.aerismill.eval.step.placement;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Vector;

import java.util.Objects;

public final class DefaultPlacementEvaluator implements PlacementEvaluator {
    private static final UnitVec3 DEFAULT_X = UnitVec3.of(new Vec3(1.0, 0.0, 0.0));
    private static final UnitVec3 DEFAULT_Y = UnitVec3.of(new Vec3(0.0, 1.0, 0.0));
    private static final UnitVec3 DEFAULT_Z = UnitVec3.of(new Vec3(0.0, 0.0, 1.0));

    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;

    public DefaultPlacementEvaluator(StepEvaluationContext context) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
    }

    @Override
    public Point3 evaluatePoint(CartesianPoint point) {
        Objects.requireNonNull(point, "point must not be null");

        Point3 cached = cache.getPoint(point.getId());
        if (cached != null) {
            return cached;
        }

        double x = requireCoordinate(point.getX(), point.getId(), "x");
        double y = requireCoordinate(point.getY(), point.getId(), "y");
        double z = requireCoordinate(point.getZ(), point.getId(), "z");

        Point3 result = new Point3(x, y, z);
        cache.putPoint(point.getId(), result);
        return result;
    }

    @Override
    public UnitVec3 evaluateDirection(Direction direction) {
        Objects.requireNonNull(direction, "direction must not be null");

        UnitVec3 cached = cache.getDirection(direction.getId());
        if (cached != null) {
            return cached;
        }

        double x = requireCoordinate(direction.getX(), direction.getId(), "x");
        double y = requireCoordinate(direction.getY(), direction.getId(), "y");
        double z = requireCoordinate(direction.getZ(), direction.getId(), "z");

        UnitVec3 result = UnitVec3.of(new Vec3(x, y, z));
        cache.putDirection(direction.getId(), result);
        return result;
    }

    @Override
    public Vec3 evaluateVector(Vector vector) {
        Objects.requireNonNull(vector, "vector must not be null");

        Vec3 cached = cache.getVector(vector.getId());
        if (cached != null) {
            return cached;
        }

        Direction orientation = vector.getOrientation();
        if (orientation == null) {
            orientation = requireDirectionEntity(vector.getOrientationRef(), vector.getId(), "orientation");
        }

        UnitVec3 unitDirection = evaluateDirection(orientation);
        Vec3 result = unitDirection.toVec3().scale(vector.getMagnitude());

        cache.putVector(vector.getId(), result);
        return result;
    }

    @Override
    public Frame3 evaluateAxis2Placement3D(Axis2Placement3D placement) {
        Objects.requireNonNull(placement, "placement must not be null");

        Frame3 cached = cache.getFrame(placement.getId());
        if (cached != null) {
            return cached;
        }

        CartesianPoint location = placement.getLocation();
        if (location == null) {
            location = requireCartesianPointEntity(placement.getLocationRef(), placement.getId(), "location");
        }

        Point3 origin = evaluatePoint(location);

        UnitVec3 zAxis = placement.getAxis() != null
                ? evaluateDirection(placement.getAxis())
                : DEFAULT_Z;

        UnitVec3 xHint = placement.getRefDirection() != null
                ? evaluateDirection(placement.getRefDirection())
                : DEFAULT_X;

        Frame3 result = buildFrame(origin, zAxis, xHint);
        cache.putFrame(placement.getId(), result);
        return result;
    }

    private Frame3 buildFrame(Point3 origin, UnitVec3 zAxis, UnitVec3 xHint) {
        Vec3 z = zAxis.toVec3();
        Vec3 xh = xHint.toVec3();

        Vec3 projectedX = xh.add(z.scale(-xh.dot(z)));
        double projectedLength = projectedX.length();

        if (projectedLength == 0.0) {
            xh = fallbackPerpendicular(zAxis).toVec3();
            projectedX = xh.add(z.scale(-xh.dot(z)));
            projectedLength = projectedX.length();

            if (projectedLength == 0.0) {
                throw new IllegalStateException("Cannot construct frame: ref direction is parallel to axis");
            }
        }

        UnitVec3 xAxis = UnitVec3.of(projectedX);
        UnitVec3 yAxis = UnitVec3.of(z.cross(xAxis.toVec3()));

        return new Frame3(origin, xAxis, yAxis, zAxis);
    }

    private UnitVec3 fallbackPerpendicular(UnitVec3 axis) {
        Vec3 z = axis.toVec3();

        Vec3 candidate = Math.abs(z.x()) < 0.9
                ? new Vec3(1.0, 0.0, 0.0)
                : new Vec3(0.0, 1.0, 0.0);

        Vec3 perpendicular = candidate.add(z.scale(-candidate.dot(z)));
        return UnitVec3.of(perpendicular);
    }

    private CartesianPoint requireCartesianPointEntity(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof CartesianPoint point)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected CARTESIAN_POINT for " + role + " ref " + ref
            );
        }
        return point;
    }

    private Direction requireDirectionEntity(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof Direction direction)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected DIRECTION for " + role + " ref " + ref
            );
        }
        return direction;
    }

    private double requireCoordinate(Double value, String entityId, String coordinateName) {
        if (value == null) {
            throw new IllegalStateException(
                    "Entity " + entityId + " is missing coordinate " + coordinateName
            );
        }
        return value;
    }
}