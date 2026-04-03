package be.doebi.aerismill.tessellation.projection;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.Point2;
import be.doebi.aerismill.tessellation.polygon.PolygonLoop2;

public interface PlaneProjector {
    Point2 project(Point3 point, PlaneSurface3 plane);
    PolygonLoop2 projectLoop(LoopGeom loop, PlaneSurface3 plane, EdgeDiscretizer edgeDiscretizer, GeometryTolerance tolerance);
}
