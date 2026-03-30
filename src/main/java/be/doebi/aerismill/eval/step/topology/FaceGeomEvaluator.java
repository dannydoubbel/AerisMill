package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.step.topology.AdvancedFace;

public interface FaceGeomEvaluator {
    FaceGeom evaluateFace(AdvancedFace advancedFace);
}