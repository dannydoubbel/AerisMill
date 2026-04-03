package be.doebi.aerismill.tessellation.curve;


import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;

import java.util.List;

public interface CurveDiscretizer {
    List<Point3> discretize(EdgeGeom edge);
}