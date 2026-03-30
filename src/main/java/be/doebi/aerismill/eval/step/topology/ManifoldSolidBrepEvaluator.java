package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.model.geom.topology.SolidGeom;
import be.doebi.aerismill.model.step.topology.ManifoldSolidBrep;

public interface ManifoldSolidBrepEvaluator {
    SolidGeom evaluateSolid(ManifoldSolidBrep manifoldSolidBrep);
}