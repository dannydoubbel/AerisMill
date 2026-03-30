package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.ClosedShell;

import java.util.List;
import java.util.Objects;

public final class DefaultShellGeomEvaluator implements ShellGeomEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final FaceGeomEvaluator faceGeomEvaluator;

    public DefaultShellGeomEvaluator(
            StepEvaluationContext context,
            FaceGeomEvaluator faceGeomEvaluator
    ) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.faceGeomEvaluator = Objects.requireNonNull(faceGeomEvaluator, "faceGeomEvaluator must not be null");
    }

    @Override
    public ShellGeom evaluateShell(ClosedShell closedShell) {
        Objects.requireNonNull(closedShell, "closedShell must not be null");

        Object cached = cache.getTopology(closedShell.getId());
        if (cached instanceof ShellGeom shellGeom) {
            return shellGeom;
        }

        List<AdvancedFace> faces = closedShell.getFaces();
        if (faces == null) {
            faces = requireFaces(closedShell);
        }

        List<FaceGeom> faceGeoms = faces.stream()
                .map(faceGeomEvaluator::evaluateFace)
                .toList();

        ShellGeom result = new ShellGeom(closedShell.getId(), faceGeoms);
        cache.putTopology(closedShell.getId(), result);
        return result;
    }

    private List<AdvancedFace> requireFaces(ClosedShell closedShell) {
        return closedShell.getFaceRefs().stream()
                .map(ref -> requireAdvancedFace(ref, closedShell.getId()))
                .toList();
    }

    private AdvancedFace requireAdvancedFace(String ref, String ownerId) {
        StepEntity entity = context.getStepModel().getEntity(ref);
        if (!(entity instanceof AdvancedFace advancedFace)) {
            throw new IllegalStateException(
                    "Entity " + ownerId + " expected ADVANCED_FACE ref " + ref
            );
        }
        return advancedFace;
    }
}