package be.doebi.aerismill.model.step.resolve;


import be.doebi.aerismill.model.step.base.StepEntityType;

public record StepResolveFailure(
        String modelName,
        String entityId,
        StepEntityType entityType,
        String exceptionType,
        String message
) {
}
