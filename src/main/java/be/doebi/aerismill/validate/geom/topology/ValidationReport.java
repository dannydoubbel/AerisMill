package be.doebi.aerismill.validate.geom.topology;

import java.util.ArrayList;
import java.util.List;

public final class ValidationReport {
    private final List<ValidationMessage> messages = new ArrayList<>();

    public List<ValidationMessage> messages() {
        return List.copyOf(messages);
    }

    public boolean isValid() {
        return messages.stream().noneMatch(message -> message.severity() == ValidationSeverity.ERROR);
    }

    public boolean hasErrors() {
        return !isValid();
    }

    public void addError(ValidationCode code, String target, String message) {
        messages.add(new ValidationMessage(
                ValidationSeverity.ERROR,
                code,
                target,
                message
        ));
    }

    public void addWarning(ValidationCode code, String target, String message) {
        messages.add(new ValidationMessage(
                ValidationSeverity.WARNING,
                code,
                target,
                message
        ));
    }

    public void addMessage(ValidationMessage message) {
        messages.add(message);
    }



    public boolean isEmpty() {
        return messages.isEmpty();
    }


}