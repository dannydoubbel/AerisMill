package be.doebi.aerismill.model.step.resolve;

import be.doebi.aerismill.model.step.base.StepModel;

public interface StepResolvable  {
    void resolveReferences(StepModel model) throws StepResolveException;

    boolean isResolved();
}
