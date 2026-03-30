package be.doebi.aerismill.eval.step.context;

import be.doebi.aerismill.model.step.base.StepModel;

public final class StepEvaluationContext {
    private final StepModel stepModel;
    private final StepEvaluationCache cache;

    public StepEvaluationContext(StepModel stepModel) {
        this(stepModel, new StepEvaluationCache());
    }

    public StepEvaluationContext(StepModel stepModel, StepEvaluationCache cache) {
        this.stepModel = stepModel;
        this.cache = cache;
    }

    public StepModel getStepModel() {
        return stepModel;
    }

    public StepEvaluationCache getCache() {
        return cache;
    }
}