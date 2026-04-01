package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.ShellGeom;

public interface ShellGeomValidator {
    ValidationReport validate(ShellGeom shell);
}