package be.doebi.aerismill.tessellation.projection;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.Point2;
import be.doebi.aerismill.tessellation.polygon.PolygonLoop2;

public  final class RecordingPlaneProjector implements PlaneProjector {
    private final java.util.List<Point3> recordedPoints = new java.util.ArrayList<>();
    private final java.util.List<PlaneSurface3> recordedPlanes = new java.util.ArrayList<>();
    private final java.util.Map<Point3, Point2> stubResults = new java.util.LinkedHashMap<>();

    @Override
    public Point2 project(Point3 point, PlaneSurface3 plane) {
        recordedPoints.add(point);
        recordedPlanes.add(plane);
        return stubResults.get(point);
    }

    @Override
    public PolygonLoop2 projectLoop(
            LoopGeom loop,
            PlaneSurface3 plane,
            EdgeDiscretizer edgeDiscretizer,
            GeometryTolerance tolerance
    ) {
        throw new UnsupportedOperationException("projectLoop not needed in this test.");
    }

    public void stubResult(Point3 point, Point2 projected) {
        stubResults.put(point, projected);
    }

    public java.util.List<Point3> recordedPoints() {
        return recordedPoints;
    }

    public java.util.List<PlaneSurface3> recordedPlanes() {
        return recordedPlanes;
    }
}