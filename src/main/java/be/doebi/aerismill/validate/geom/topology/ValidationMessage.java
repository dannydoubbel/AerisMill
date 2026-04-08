package be.doebi.aerismill.validate.geom.topology;

public record ValidationMessage(
        ValidationSeverity severity,
        ValidationCode code,
        String target,
        String message
) {}

