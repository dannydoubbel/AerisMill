package be.doebi.aerismill.model.step.resolve;

public class StepResolveException extends RuntimeException {
    public StepResolveException(String message) {
        super(message);
    }

    public StepResolveException(String message, Throwable cause) {
        super(message, cause);
    }
}
