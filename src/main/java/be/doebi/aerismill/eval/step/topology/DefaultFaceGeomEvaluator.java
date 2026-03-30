package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.surface.SurfaceEvaluator;
import be.doebi.aerismill.model.geom.surface.Surface3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.CylindricalSurface;
import be.doebi.aerismill.model.step.geometry.Plane;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.EdgeLoop;
import be.doebi.aerismill.model.step.topology.FaceBound;
import be.doebi.aerismill.model.step.topology.FaceOuterBound;

import java.util.List;
import java.util.Objects;

public final class DefaultFaceGeomEvaluator implements FaceGeomEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final SurfaceEvaluator surfaceEvaluator;
    private final LoopGeomEvaluator loopGeomEvaluator;

    public DefaultFaceGeomEvaluator(
            StepEvaluationContext context,
            SurfaceEvaluator surfaceEvaluator,
            LoopGeomEvaluator loopGeomEvaluator
    ) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.surfaceEvaluator = Objects.requireNonNull(surfaceEvaluator, "surfaceEvaluator must not be null");
        this.loopGeomEvaluator = Objects.requireNonNull(loopGeomEvaluator, "loopGeomEvaluator must not be null");
    }

    @Override
    public FaceGeom evaluateFace(AdvancedFace advancedFace) {
        Objects.requireNonNull(advancedFace, "advancedFace must not be null");

        Object cached = cache.getTopology(advancedFace.getId());
        if (cached instanceof FaceGeom faceGeom) {
            return faceGeom;
        }

        StepEntity faceGeometry = advancedFace.getFaceGeometry();
        if (faceGeometry == null) {
            faceGeometry = requireStepEntity(
                    advancedFace.getFaceGeometryRef(),
                    advancedFace.getId(),
                    "faceGeometry"
            );
        }

        Surface3 surface = evaluateSurface(faceGeometry, advancedFace.getId());

        List<StepEntity> bounds = advancedFace.getBounds();
        if (bounds == null || bounds.isEmpty()) {
            bounds = requireBounds(advancedFace);
        }

        List<LoopGeom> loopGeoms = bounds.stream()
                .map(bound -> evaluateBoundLoop(bound, advancedFace.getId()))
                .toList();

        FaceGeom result = new FaceGeom(
                advancedFace.getId(),
                surface,
                loopGeoms,
                advancedFace.isSameSense()
        );

        cache.putTopology(advancedFace.getId(), result);
        return result;
    }

    private Surface3 evaluateSurface(StepEntity faceGeometry, String ownerId) {
        if (faceGeometry instanceof Plane plane) {
            return surfaceEvaluator.evaluatePlane(plane);
        }
        if (faceGeometry instanceof CylindricalSurface cylindricalSurface) {
            return surfaceEvaluator.evaluateCylindricalSurface(cylindricalSurface);
        }

        throw new IllegalStateException(
                "Entity " + ownerId + " has unsupported face geometry type: " + faceGeometry.getType()
        );
    }

    private List<StepEntity> requireBounds(AdvancedFace advancedFace) {
        return advancedFace.getBoundRefs().stream()
                .map(ref -> requireStepEntity(ref, advancedFace.getId(), "bound"))
                .toList();
    }

    private LoopGeom evaluateBoundLoop(StepEntity boundEntity, String ownerId) {
        if (boundEntity instanceof FaceOuterBound faceOuterBound) {
            EdgeLoop bound = faceOuterBound.getBound();
            if (bound == null) {
                bound = requireEdgeLoop(faceOuterBound.getBoundRef(), ownerId, "outer bound");
            }
            return loopGeomEvaluator.evaluateLoop(bound);
        }

        if (boundEntity instanceof FaceBound faceBound) {
            EdgeLoop bound = faceBound.getBound();
            if (bound == null) {
                bound = requireEdgeLoop(faceBound.getBoundRef(), ownerId, "inner bound");
            }
            return loopGeomEvaluator.evaluateLoop(bound);
        }

        throw new IllegalStateException(
                "Entity " + ownerId + " has unsupported bound type: " + boundEntity.getType()
        );
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

    private EdgeLoop requireEdgeLoop(String ref, String ownerId, String role) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof EdgeLoop edgeLoop)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected EDGE_LOOP for " + role + " ref " + ref
            );
        }
        return edgeLoop;
    }
}