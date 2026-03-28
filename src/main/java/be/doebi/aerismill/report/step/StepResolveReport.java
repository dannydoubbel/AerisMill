package be.doebi.aerismill.report.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.resolve.StepResolveFailure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepResolveReport {
    private final String modelName;
    private int successCount;
    private int failureCount;
    private final Map<StepEntityType, Integer> failureCountsByType = new HashMap<>();
    private final List<StepResolveFailure> failures = new ArrayList<>();

    public StepResolveReport(String modelName) {
        this.modelName = modelName;
    }

    public void recordSuccess(StepEntity entity) {
        successCount++;
    }

    public void recordFailure(StepEntity entity, Exception exception) {
        failureCount++;
        failureCountsByType.merge(entity.getType(), 1, Integer::sum);

        failures.add(new StepResolveFailure(
                modelName,
                entity.getId(),
                entity.getType(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
        ));
    }

    public String getModelName() {
        return modelName;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public Map<StepEntityType, Integer> getFailureCountsByType() {
        return failureCountsByType;
    }

    public List<StepResolveFailure> getFailures() {
        return failures;
    }
}