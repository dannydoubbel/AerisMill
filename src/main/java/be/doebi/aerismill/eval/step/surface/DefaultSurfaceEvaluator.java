package be.doebi.aerismill.eval.step.surface;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.surface.*;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepLogical;
import be.doebi.aerismill.model.step.geometry.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DefaultSurfaceEvaluator implements SurfaceEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final PlacementEvaluator placementEvaluator;

    public DefaultSurfaceEvaluator(StepEvaluationContext context, PlacementEvaluator placementEvaluator) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.placementEvaluator = Objects.requireNonNull(placementEvaluator, "placementEvaluator must not be null");
    }

    @Override
    public Surface3 evaluate(Plane plane) {
        return evaluatePlane(plane);
    }

    @Override
    public Surface3 evaluate(ConicalSurface conicalSurface) {
        return evaluateConicalSurface(conicalSurface);
    }

    @Override
    public Surface3 evaluate(CylindricalSurface cylindricalSurface) {
        return evaluateCylindricalSurface(cylindricalSurface);
    }

    @Override
    public Surface3 evaluate(BSplineSurfaceWithKnots bSplineSurfaceWithKnots) {
        return evaluateBSplineSurfaceWithKnots(bSplineSurfaceWithKnots);
    }

    @Override
    public Surface3 evaluate(SphericalSurface sphericalSurface) {
        return evaluateSphericalSurface(sphericalSurface);
    }

    @Override
    public Surface3 evaluate(ToroidalSurface toroidalSurface) {
        return evaluateToroidalSurface(toroidalSurface);
    }

    @Override
    public ToroidalSurface3 evaluateToroidalSurface(ToroidalSurface toroidalSurface) {
        Objects.requireNonNull(toroidalSurface, "toroidalSurface must not be null");

        Surface3 cached = cache.getSurface(toroidalSurface.getId());
        if (cached instanceof ToroidalSurface3 toroidalSurface3) {
            return toroidalSurface3;
        }

        Axis2Placement3D position = toroidalSurface.getPosition();
        if (position == null) {
            position = requireAxis2Placement3D(
                    toroidalSurface.getPositionRef(),
                    toroidalSurface.getId(),
                    "position"
            );
        }

        Frame3 frame = placementEvaluator.evaluateAxis2Placement3D(position);
        ToroidalSurface3 result = new ToroidalSurface3(
                frame,
                toroidalSurface.getMajorRadius(),
                toroidalSurface.getMinorRadius()
        );

        cache.putSurface(toroidalSurface.getId(), result);
        return result;
    }


    @Override
    public SphericalSurface3 evaluateSphericalSurface(SphericalSurface sphericalSurface) {
        Objects.requireNonNull(sphericalSurface, "sphericalSurface must not be null");

        Surface3 cached = cache.getSurface(sphericalSurface.getId());
        if (cached instanceof SphericalSurface3 sphericalSurface3) {
            return sphericalSurface3;
        }

        Axis2Placement3D position = sphericalSurface.getPosition();
        if (position == null) {
            position = requireAxis2Placement3D(
                    sphericalSurface.getPositionRef(),
                    sphericalSurface.getId(),
                    "position"
            );
        }

        Frame3 frame = placementEvaluator.evaluateAxis2Placement3D(position);
        SphericalSurface3 result = new SphericalSurface3(frame, sphericalSurface.getRadius());

        cache.putSurface(sphericalSurface.getId(), result);
        return result;
    }


    @Override
    public PlaneSurface3 evaluatePlane(Plane plane) {
        Objects.requireNonNull(plane, "plane must not be null");

        Surface3 cached = cache.getSurface(plane.getId());
        if (cached instanceof PlaneSurface3 planeSurface3) {
            return planeSurface3;
        }

        Axis2Placement3D position = plane.getPosition();
        if (position == null) {
            position = requireAxis2Placement3D(plane.getPositionRef(), plane.getId(), "position");
        }

        Frame3 frame = placementEvaluator.evaluateAxis2Placement3D(position);
        PlaneSurface3 result = new PlaneSurface3(frame);

        cache.putSurface(plane.getId(), result);
        return result;
    }

    @Override
    public ConicalSurface3 evaluateConicalSurface(ConicalSurface conicalSurface) {
        Objects.requireNonNull(conicalSurface, "conicalSurface must not be null");

        Surface3 cached = cache.getSurface(conicalSurface.getId());
        if (cached instanceof ConicalSurface3 conicalSurface3) {
            return conicalSurface3;
        }

        Axis2Placement3D position = conicalSurface.getPosition();
        if (position == null) {
            position = requireAxis2Placement3D(
                    conicalSurface.getPositionRef(),
                    conicalSurface.getId(),
                    "position"
            );
        }

        Frame3 frame = placementEvaluator.evaluateAxis2Placement3D(position);
        ConicalSurface3 result = new ConicalSurface3(
                frame,
                conicalSurface.getRadius(),
                conicalSurface.getSemiAngle()
        );

        cache.putSurface(conicalSurface.getId(), result);
        return result;
    }

    @Override
    public CylindricalSurface3 evaluateCylindricalSurface(CylindricalSurface cylindricalSurface) {
        Objects.requireNonNull(cylindricalSurface, "cylindricalSurface must not be null");

        Surface3 cached = cache.getSurface(cylindricalSurface.getId());
        if (cached instanceof CylindricalSurface3 cylindricalSurface3) {
            return cylindricalSurface3;
        }

        Axis2Placement3D position = cylindricalSurface.getPosition();
        if (position == null) {
            position = requireAxis2Placement3D(
                    cylindricalSurface.getPositionRef(),
                    cylindricalSurface.getId(),
                    "position"
            );
        }

        Frame3 frame = placementEvaluator.evaluateAxis2Placement3D(position);
        CylindricalSurface3 result = new CylindricalSurface3(frame, cylindricalSurface.getRadius());

        cache.putSurface(cylindricalSurface.getId(), result);
        return result;
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

    @Override
    public BSplineSurface3 evaluateBSplineSurfaceWithKnots(BSplineSurfaceWithKnots splineSurface) {
        Objects.requireNonNull(splineSurface, "splineSurface must not be null");

        Surface3 cached = cache.getSurface(splineSurface.getId());
        if (cached instanceof BSplineSurface3 bSplineSurface3) {
            return bSplineSurface3;
        }

        List<List<Point3>> controlPoints = toPointGrid(splineSurface);
        List<Double> expandedUKnots = expandKnots(
                splineSurface.getUMultiplicities(),
                splineSurface.getUKnots(),
                splineSurface.getId(),
                "u"
        );
        List<Double> expandedVKnots = expandKnots(
                splineSurface.getVMultiplicities(),
                splineSurface.getVKnots(),
                splineSurface.getId(),
                "v"
        );

        BSplineSurface3 result = new BSplineSurface3(
                splineSurface.getUDegree(),
                splineSurface.getVDegree(),
                controlPoints,
                expandedUKnots,
                expandedVKnots,
                splineSurface.isUClosed() == StepLogical.TRUE,
                splineSurface.isVClosed() == StepLogical.TRUE,
                splineSurface.isSelfIntersect() == StepLogical.TRUE,
                splineSurface.getSurfaceForm(),
                splineSurface.getKnotSpec()
        );

        cache.putSurface(splineSurface.getId(), result);
        return result;
    }

    private List<List<Point3>> toPointGrid(BSplineSurfaceWithKnots splineSurface) {
        List<List<Point3>> result = new ArrayList<>();

        for (List<StepEntity> row : splineSurface.getControlPointsList()) {
            List<Point3> pointRow = new ArrayList<>();

            for (StepEntity entity : row) {
                if (!(entity instanceof CartesianPoint point)) {
                    throw new IllegalStateException(
                            "B_SPLINE_SURFACE_WITH_KNOTS " + splineSurface.getId() +
                                    " expected CARTESIAN_POINT but found " +
                                    (entity == null ? "null" : entity.getType())
                    );
                }

                pointRow.add(placementEvaluator.evaluatePoint(point));
            }

            result.add(List.copyOf(pointRow));
        }

        return List.copyOf(result);
    }

    private List<Double> expandKnots(List<Integer> multiplicities, List<Double> knots, String ownerId, String axisName) {
        if (multiplicities == null || knots == null) {
            throw new IllegalStateException(
                    "Spline surface " + ownerId + " has null " + axisName + " knot data"
            );
        }
        if (multiplicities.size() != knots.size()) {
            throw new IllegalStateException(
                    "Spline surface " + ownerId + " " + axisName +
                            " knot multiplicities size does not match knot values size"
            );
        }

        List<Double> expanded = new ArrayList<>();
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