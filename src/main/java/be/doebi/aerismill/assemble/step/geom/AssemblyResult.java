package be.doebi.aerismill.assemble.step.geom;


import java.util.List;

public record AssemblyResult(
        List<SolidAssemblyResult> solids,
        List<AssemblyIssue> issues
) {}