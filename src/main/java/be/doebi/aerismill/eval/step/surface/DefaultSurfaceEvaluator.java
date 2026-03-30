package be.doebi.aerismill.eval.step.surface;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.surface.CylindricalSurface3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.surface.Surface3;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CylindricalSurface;
import be.doebi.aerismill.model.step.geometry.Plane;

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
    public Surface3 evaluate(CylindricalSurface cylindricalSurface) {
        return evaluateCylindricalSurface(cylindricalSurface);
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
}