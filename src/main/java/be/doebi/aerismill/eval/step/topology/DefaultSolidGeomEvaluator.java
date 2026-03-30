package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.topology.ClosedShell;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;

import java.util.Objects;

public final class DefaultSolidGeomEvaluator implements SolidGeomEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final ShellGeomEvaluator shellGeomEvaluator;

    public DefaultSolidGeomEvaluator(
            StepEvaluationContext context,
            ShellGeomEvaluator shellGeomEvaluator
    ) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.shellGeomEvaluator = Objects.requireNonNull(shellGeomEvaluator, "shellGeomEvaluator must not be null");
    }

    @Override
    public SolidGeom evaluateSolid(ManifoldSolidBrep manifoldSolidBrep) {
        Objects.requireNonNull(manifoldSolidBrep, "manifoldSolidBrep must not be null");

        Object cached = cache.getTopology(manifoldSolidBrep.getId());
        if (cached instanceof SolidGeom solidGeom) {
            return solidGeom;
        }

        ClosedShell outer = manifoldSolidBrep.getOuter();
        if (outer == null) {
            outer = requireOuter(manifoldSolidBrep);
        }

        ShellGeom outerShell = shellGeomEvaluator.evaluateShell(outer);

        SolidGeom result = new SolidGeom(manifoldSolidBrep.getId(), outerShell);
        cache.putTopology(manifoldSolidBrep.getId(), result);
        return result;
    }

    private ClosedShell requireOuter(ManifoldSolidBrep manifoldSolidBrep) {
        String outerRef = manifoldSolidBrep.getOuterRef();
        if (outerRef == null) {
            throw new IllegalStateException(
                    "Entity " + manifoldSolidBrep.getId() + " has no outer shell reference"
            );
        }

        StepEntity entity = context.getStepModel().getEntity(outerRef);
        if (!(entity instanceof ClosedShell closedShell)) {
            throw new IllegalStateException(
                    "Entity " + manifoldSolidBrep.getId() + " expected CLOSED_SHELL ref " + outerRef
            );
        }
        return closedShell;
    }
}