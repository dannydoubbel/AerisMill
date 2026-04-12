package be.doebi.aerismill.tessellation.curve;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DefaultEdgeDiscretizer implements EdgeDiscretizer {

    private final CurveDiscretizer curveDiscretizer;

    public DefaultEdgeDiscretizer() {
        this(new DefaultCurveDiscretizer());
    }

    public DefaultEdgeDiscretizer(CurveDiscretizer curveDiscretizer) {
        this.curveDiscretizer = Objects.requireNonNull(curveDiscretizer, "curveDiscretizer must not be null");
    }

    @Override
    public List<Point3> discretize(OrientedEdgeGeom edge, GeometryTolerance tolerance) {
        if (edge == null) {
            throw new IllegalArgumentException("Edge must not be null.");
        }

        List<Point3> points = new ArrayList<>(curveDiscretizer.discretize(edge.edge()));

        if (!edge.orientation()) {
            Collections.reverse(points);
        }

        return points;
    }
}