package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.Point2;
import be.doebi.aerismill.tessellation.polygon.PolygonLoop2;
import be.doebi.aerismill.tessellation.polygon.PolygonTriangulator;
import be.doebi.aerismill.tessellation.polygon.PolygonWithHoles2;
import be.doebi.aerismill.tessellation.projection.PlaneProjector;

import java.util.ArrayList;
import java.util.List;

public class PlanarFaceTessellator implements FaceTessellator {

    private final EdgeDiscretizer edgeDiscretizer;
    private final PolygonTriangulator polygonTriangulator;
    private final PlaneProjector planeProjector;
    private final GeometryTolerance tolerance;

    public PlanarFaceTessellator(
            EdgeDiscretizer edgeDiscretizer,
            PolygonTriangulator polygonTriangulator,
            PlaneProjector planeProjector,
            GeometryTolerance tolerance
    ) {
        this.edgeDiscretizer = edgeDiscretizer;
        this.polygonTriangulator = polygonTriangulator;
        this.planeProjector = planeProjector;
        this.tolerance = tolerance;
    }

    @Override
    public FaceMeshPatch tessellate(FaceGeom face) {
        if (!(face.surface() instanceof PlaneSurface3)) {
            throw new IllegalArgumentException("Only planar faces are supported for now.");
        }
        if (face.bounds() == null || face.bounds().isEmpty()) {
            throw new IllegalArgumentException("Face must have at least one bound.");
        }
        if (face.bounds().size() > 1) {
            throw new IllegalArgumentException("Only single-bound planar faces are supported for now.");
        }

        LoopGeom outerBound = face.bounds().getFirst();

        if (outerBound.edges() == null || outerBound.edges().isEmpty()) {
            throw new IllegalArgumentException("Face bound must contain at least one edge.");
        }

        var discretizedEdgePointLists = collectDiscretizedEdgePoints(outerBound);
        var boundaryPoints = flattenDiscretizedEdgePointLists(discretizedEdgePointLists);
        var cleanedBoundaryPoints = collapseConsecutiveDuplicateBoundaryPoints(boundaryPoints);
        var openBoundaryPoints = removeClosingDuplicateBoundaryPoint(cleanedBoundaryPoints);
        var projectedBoundaryPoints = projectBoundaryPointsTo2D((PlaneSurface3) face.surface(), openBoundaryPoints);
        var polygonLoop = buildOuterPolygonLoop(projectedBoundaryPoints);
        var polygon = buildPolygonWithNoHoles(polygonLoop);
        var triangles = triangulatePolygon(polygon);

        return buildFaceMeshPatch(openBoundaryPoints, triangles);


    }

    public List<Point3> collapseConsecutiveDuplicateBoundaryPoints(List<Point3> boundaryPoints) {
        List<Point3> collapsed = new ArrayList<>();

        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return collapsed;
        }

        Point3 previous = null;

        for (Point3 point : boundaryPoints) {
            if (point == null) {
                continue;
            }

            if (!point.equals(previous)) {
                collapsed.add(point);
                previous = point;
            }
        }

        return collapsed;
    }


    List<List<Point3>> collectDiscretizedEdgePoints(LoopGeom outerBound) {
        if (outerBound.edges() == null || outerBound.edges().isEmpty()) {
            throw new IllegalArgumentException("Face bound must contain at least one edge.");
        }

        java.util.List<java.util.List<be.doebi.aerismill.model.geom.math.Point3>> discretizedEdges = new java.util.ArrayList<>();
        boolean foundNonNullEdge = false;

        for (var edge : outerBound.edges()) {
            if (edge != null) {
                discretizedEdges.add(edgeDiscretizer.discretize(edge, tolerance));
                foundNonNullEdge = true;
            }
        }

        if (!foundNonNullEdge) {
            throw new IllegalArgumentException("Face bound must contain at least one non-null edge.");
        }

        return discretizedEdges;
    }

    List<Point3> flattenDiscretizedEdgePointLists(
            java.util.List<java.util.List<be.doebi.aerismill.model.geom.math.Point3>> discretizedEdgePointLists
    ) {
        java.util.List<be.doebi.aerismill.model.geom.math.Point3> flattened = new java.util.ArrayList<>();

        if (discretizedEdgePointLists == null || discretizedEdgePointLists.isEmpty()) {
            return flattened;
        }

        for (var edgePoints : discretizedEdgePointLists) {
            if (edgePoints != null && !edgePoints.isEmpty()) {
                flattened.addAll(edgePoints);
            }
        }

        return flattened;
    }

    List<Point2> projectBoundaryPointsTo2D(PlaneSurface3 plane,List<Point3> boundaryPoints
    ) {
        java.util.List<be.doebi.aerismill.tessellation.polygon.Point2> projectedPoints = new java.util.ArrayList<>();

        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return projectedPoints;
        }

        for (var point : boundaryPoints) {
            projectedPoints.add(planeProjector.project(point, plane));
        }

        return projectedPoints;
    }

    PolygonLoop2 buildOuterPolygonLoop( List<Point2> projectedBoundaryPoints    ) {
        if (projectedBoundaryPoints == null) {
            throw new IllegalArgumentException("Projected boundary points must not be null.");
        }
        return new PolygonLoop2(projectedBoundaryPoints);
    }

    PolygonWithHoles2 buildPolygonWithNoHoles(PolygonLoop2 outerLoop) {
        if (outerLoop == null) {
            throw new IllegalArgumentException("Outer polygon loop must not be null.");
        }
        return new PolygonWithHoles2(outerLoop, List.of());
    }

    List<int[]> triangulatePolygon(PolygonWithHoles2 polygon) {
        if (polygon == null) {
            throw new IllegalArgumentException("Polygon must not be null.");
        }
        return polygonTriangulator.triangulate(polygon);
    }

    FaceMeshPatch buildFaceMeshPatch(List<Point3> boundaryPoints, List<int[]> triangleIndices) {
        if (boundaryPoints == null) {
            throw new IllegalArgumentException("Boundary points must not be null.");
        }
        if (triangleIndices == null) {
            throw new IllegalArgumentException("Triangle indices must not be null.");
        }

        return new FaceMeshPatch(boundaryPoints, triangleIndices);
    }

    List<Point3> removeClosingDuplicateBoundaryPoint(List<Point3> boundaryPoints) {
        List<Point3> result = new ArrayList<>();

        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return result;
        }

        if (boundaryPoints.size() == 1) {
            result.addAll(boundaryPoints);
            return result;
        }

        result.addAll(boundaryPoints);

        Point3 first = result.getFirst();
        Point3 last = result.getLast();

        if (first != null && first.equals(last)) {
            result.removeLast();
        }

        return result;
    }


}