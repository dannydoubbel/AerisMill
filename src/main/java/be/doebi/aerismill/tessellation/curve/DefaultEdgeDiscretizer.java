package be.doebi.aerismill.tessellation.curve;


import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;

import java.util.List;

public class DefaultEdgeDiscretizer implements EdgeDiscretizer {

    @Override
    public List<Point3> discretize(OrientedEdgeGeom edge, GeometryTolerance tolerance) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge must not be null.");
        }

        return List.of(edge.start(), edge.end());
    }
}