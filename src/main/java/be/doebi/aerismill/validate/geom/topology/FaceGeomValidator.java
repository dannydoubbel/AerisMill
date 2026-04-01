package be.doebi.aerismill.validate.geom.topology;

import be.doebi.aerismill.model.geom.topology.FaceGeom;

public interface FaceGeomValidator {
    ValidationReport validate(FaceGeom face);
}