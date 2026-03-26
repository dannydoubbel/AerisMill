package be.doebi.aerismill.model.step.base;

import be.doebi.aerismill.model.step.resolve.ResolveState;
import be.doebi.aerismill.model.step.resolve.StepResolvable;
import be.doebi.aerismill.model.step.resolve.StepResolveException;

public abstract class ResolvableStepEntity extends StepEntity implements StepResolvable {

    private ResolveState resolveState = ResolveState.UNRESOLVED;

    protected ResolvableStepEntity(String id, StepEntityType type, String rawParameters) {
        super(id, type, rawParameters);
    }

    @Override
    public final void resolveReferences(StepModel model) {
        if (resolveState == ResolveState.RESOLVED) {
            return;
        }

        if (resolveState == ResolveState.RESOLVING) {
            throw new StepResolveException("Circular STEP resolve detected for " + getId());
        }

        resolveState = ResolveState.RESOLVING;
        doResolve(model);
        resolveState = ResolveState.RESOLVED;
    }

    protected abstract void doResolve(StepModel model);

    @Override
    public boolean isResolved() {
        return resolveState == ResolveState.RESOLVED;
    }
}