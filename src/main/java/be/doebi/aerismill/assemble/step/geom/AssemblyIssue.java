package be.doebi.aerismill.assemble.step.geom;

public record AssemblyIssue(
        String stepId,
        AssemblyIssueSeverity severity,
        AssemblyIssueCode code,
        String message
) {}