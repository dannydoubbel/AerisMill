package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.geom.topology.SolidGeom;

public interface TopologyValidationService {
    ValidationReport validateLoop(LoopGeom loop);

    ValidationReport validateFace(FaceGeom face);

    ValidationReport validateShell(ShellGeom shell);

    ValidationReport validateSolid(SolidGeom solid);
}