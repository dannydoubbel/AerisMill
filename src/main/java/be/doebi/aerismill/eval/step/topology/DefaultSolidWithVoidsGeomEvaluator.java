package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationCache;
import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidWithVoidsGeom;
import be.doebi.aerismill.model.step.topology.BrepWithVoids;
import be.doebi.aerismill.model.step.topology.ClosedShell;

import java.util.List;
import java.util.Objects;

public final class DefaultSolidWithVoidsGeomEvaluator implements SolidWithVoidsGeomEvaluator {
    private final StepEvaluationContext context;
    private final StepEvaluationCache cache;
    private final ShellGeomEvaluator shellGeomEvaluator;

    public DefaultSolidWithVoidsGeomEvaluator(
            StepEvaluationContext context,
            ShellGeomEvaluator shellGeomEvaluator
    ) {
        this.context = Objects.requireNonNull(context, "context must not be null");
        this.cache = Objects.requireNonNull(context.getCache(), "context cache must not be null");
        this.shellGeomEvaluator = Objects.requireNonNull(shellGeomEvaluator, "shellGeomEvaluator must not be null");
    }

    @Override
    public SolidWithVoidsGeom evaluateSolid(BrepWithVoids brepWithVoids) {
        Objects.requireNonNull(brepWithVoids, "brepWithVoids must not be null");

        Object cached = cache.getTopology(brepWithVoids.getId());
        if (cached instanceof SolidWithVoidsGeom solidWithVoidsGeom) {
            return solidWithVoidsGeom;
        }

        ClosedShell outer = Objects.requireNonNull(
                brepWithVoids.getOuter(),
                "brepWithVoids outer must not be null"
        );

        List<ClosedShell> voids = brepWithVoids.getVoids();
        if (voids == null) {
            voids = List.of();
        }

        ShellGeom outerShell = shellGeomEvaluator.evaluateShell(outer);
        List<ShellGeom> voidShells = voids.stream()
                .map(shellGeomEvaluator::evaluateShell)
                .toList();

        SolidWithVoidsGeom result = new SolidWithVoidsGeom(
                brepWithVoids.getId(),
                outerShell,
                voidShells
        );

        cache.putTopology(brepWithVoids.getId(), result);
        return result;
    }
}