package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.step.topology.ClosedShell;

public interface ShellGeomEvaluator {
    ShellGeom evaluateShell(ClosedShell closedShell);
}