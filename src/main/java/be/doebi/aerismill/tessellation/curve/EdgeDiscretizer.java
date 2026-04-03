package be.doebi.aerismill.tessellation.curve;


import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;

import java.util.List;

public interface EdgeDiscretizer {
    List<Point3> discretize(OrientedEdgeGeom edge, GeometryTolerance tolerance);
}