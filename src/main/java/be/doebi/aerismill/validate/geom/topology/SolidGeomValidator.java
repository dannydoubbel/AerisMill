package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.SolidGeom;

public interface SolidGeomValidator {
    ValidationReport validate(SolidGeom solid);
}