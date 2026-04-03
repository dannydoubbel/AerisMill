package be.doebi.aerismill.tessellation.projection;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.Point2;
import be.doebi.aerismill.tessellation.polygon.PolygonLoop2;

public class DefaultPlaneProjector implements PlaneProjector {

    @Override
    public Point2 project(Point3 point, PlaneSurface3 plane) {
        Point3 origin = plane.frame().origin();
        Vec3 delta = point.subtract(origin);

        double x = delta.dot(plane.frame().xAxis().toVec3());
        double y = delta.dot(plane.frame().yAxis().toVec3());

        return new Point2(x, y);
    }

    @Override
    public PolygonLoop2 projectLoop(
            LoopGeom loop,
            PlaneSurface3 plane,
            EdgeDiscretizer edgeDiscretizer,
            GeometryTolerance tolerance
    ) {
        throw new UnsupportedOperationException("projectLoop not implemented yet.");
    }
}