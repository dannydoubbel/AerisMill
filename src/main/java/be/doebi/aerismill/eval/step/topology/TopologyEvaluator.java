package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.step.topology.AdvancedFace;
import be.doebi.aerismill.model.step.topology.ClosedShell;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.EdgeLoop;

public interface TopologyEvaluator {
    EdgeGeom evaluateEdgeCurve(EdgeCurve edgeCurve);
    LoopGeom evaluateEdgeLoop(EdgeLoop edgeLoop);
    FaceGeom evaluateAdvancedFace(AdvancedFace face);
    ShellGeom evaluateClosedShell(ClosedShell shell);
}