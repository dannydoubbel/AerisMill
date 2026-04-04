package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
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
        if (!(face.surface() instanceof PlaneSurface3 plane)) {
            throw new IllegalArgumentException("Only planar faces are supported for now.");
        }
        if (face.bounds() == null || face.bounds().isEmpty()) {
            throw new IllegalArgumentException("Face must have at least one bound.");
        }

        List<PreparedLoop> preparedLoops = prepareProjectedPolygonLoops(face.bounds(), plane);
        PolygonWithHoles2 polygon = buildPolygonWithHoles(preparedLoops);
        validateHoleRelationships(polygon);

        List<int[]> triangles = triangulatePolygon(polygon);

        List<Point3> boundaryPoints = collectPreparedBoundaryPoints(preparedLoops);
        List<Point2> projectedBoundaryPoints = collectPreparedProjectedPoints(preparedLoops);

        validateTrianglesNotEmpty(triangles);
        validateTriangleIndices(boundaryPoints, triangles);
        validateTrianglesAreNonDegenerate(triangles);
        validateTrianglesHavePositiveArea(projectedBoundaryPoints, triangles);

        return buildFaceMeshPatch(boundaryPoints, triangles);
    }

    List<Point3> collapseConsecutiveDuplicateBoundaryPoints(List<Point3> boundaryPoints) {
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

        List<List<Point3>> discretizedEdges = new ArrayList<>();
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

    List<Point3> flattenDiscretizedEdgePointLists(List<List<Point3>> discretizedEdgePointLists) {
        List<Point3> flattened = new ArrayList<>();

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

    List<Point2> projectBoundaryPointsTo2D(PlaneSurface3 plane, List<Point3> boundaryPoints) {
        List<Point2> projectedPoints = new ArrayList<>();

        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return projectedPoints;
        }

        for (var point : boundaryPoints) {
            Point2 projected = planeProjector.project(point, plane);
            if (projected == null) {
                throw new IllegalArgumentException("Projected boundary points must not contain null points.");
            }
            projectedPoints.add(projected);
        }

        return projectedPoints;
    }

    PolygonLoop2 buildOuterPolygonLoop(List<Point2> projectedBoundaryPoints) {
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

    void validateBoundaryHasAtLeastThreePoints(List<Point3> boundaryPoints) {
        if (boundaryPoints == null || boundaryPoints.size() < 3) {
            throw new IllegalArgumentException("Boundary must contain at least three points for triangulation.");
        }
    }

    void validateTrianglesNotEmpty(List<int[]> triangleIndices) {
        if (triangleIndices == null || triangleIndices.isEmpty()) {
            throw new IllegalArgumentException("Triangulation must produce at least one triangle.");
        }
    }

    void validateTriangleIndices(List<Point3> boundaryPoints, List<int[]> triangleIndices) {
        if (boundaryPoints == null) {
            throw new IllegalArgumentException("Boundary points must not be null.");
        }
        if (triangleIndices == null) {
            throw new IllegalArgumentException("Triangle indices must not be null.");
        }

        int vertexCount = boundaryPoints.size();

        for (int[] triangle : triangleIndices) {
            if (triangle == null || triangle.length != 3) {
                throw new IllegalArgumentException("Each triangle must contain exactly three vertex indices.");
            }

            for (int index : triangle) {
                if (index < 0 || index >= vertexCount) {
                    throw new IllegalArgumentException("Triangle index out of bounds for boundary vertices.");
                }
            }
        }
    }

    void validateTrianglesAreNonDegenerate(List<int[]> triangleIndices) {
        if (triangleIndices == null) {
            throw new IllegalArgumentException("Triangle indices must not be null.");
        }

        for (int[] triangle : triangleIndices) {
            if (triangle == null || triangle.length != 3) {
                throw new IllegalArgumentException("Each triangle must contain exactly three vertex indices.");
            }

            if (triangle[0] == triangle[1] || triangle[0] == triangle[2] || triangle[1] == triangle[2]) {
                throw new IllegalArgumentException("Triangle must reference three distinct vertex indices.");
            }
        }
    }

    void validateTrianglesHavePositiveArea(List<Point2> projectedBoundaryPoints, List<int[]> triangleIndices) {
        if (projectedBoundaryPoints == null) {
            throw new IllegalArgumentException("Projected boundary points must not be null.");
        }
        if (triangleIndices == null) {
            throw new IllegalArgumentException("Triangle indices must not be null.");
        }

        for (int[] triangle : triangleIndices) {
            if (triangle == null || triangle.length != 3) {
                throw new IllegalArgumentException("Each triangle must contain exactly three vertex indices.");
            }

            Point2 a = projectedBoundaryPoints.get(triangle[0]);
            Point2 b = projectedBoundaryPoints.get(triangle[1]);
            Point2 c = projectedBoundaryPoints.get(triangle[2]);

            double twiceSignedArea =
                    (b.x() - a.x()) * (c.y() - a.y()) -
                            (b.y() - a.y()) * (c.x() - a.x());

            if (twiceSignedArea == 0.0) {
                throw new IllegalArgumentException("Triangle must have non-zero area in projected space.");
            }
        }
    }

    void validateProjectedBoundaryIsSimple(PolygonLoop2 outerLoop) {
        if (outerLoop == null) {
            throw new IllegalArgumentException("Projected boundary loop must not be null.");
        }
        if (outerLoop.points() == null) {
            throw new IllegalArgumentException("Projected boundary loop points must not be null.");
        }

        List<Point2> points = outerLoop.points();

        if (points.size() < 4) {
            return;
        }

        int segmentCount = points.size();

        for (int i = 0; i < segmentCount; i++) {
            Point2 a1 = points.get(i);
            Point2 a2 = points.get((i + 1) % segmentCount);

            for (int j = i + 1; j < segmentCount; j++) {
                if (j == i + 1) {
                    continue;
                }

                if (i == 0 && j == segmentCount - 1) {
                    continue;
                }

                Point2 b1 = points.get(j);
                Point2 b2 = points.get((j + 1) % segmentCount);

                if (segmentsIntersect(a1, a2, b1, b2)) {
                    throw new IllegalArgumentException("Projected boundary must not self-intersect.");
                }
            }
        }
    }

    private boolean segmentsIntersect(Point2 p1, Point2 p2, Point2 q1, Point2 q2) {
        double o1 = orientation(p1, p2, q1);
        double o2 = orientation(p1, p2, q2);
        double o3 = orientation(q1, q2, p1);
        double o4 = orientation(q1, q2, p2);

        if (((o1 > 0 && o2 < 0) || (o1 < 0 && o2 > 0)) &&
                ((o3 > 0 && o4 < 0) || (o3 < 0 && o4 > 0))) {
            return true;
        }

        if (o1 == 0.0 && onSegment(p1, q1, p2)) {
            return true;
        }
        if (o2 == 0.0 && onSegment(p1, q2, p2)) {
            return true;
        }
        if (o3 == 0.0 && onSegment(q1, p1, q2)) {
            return true;
        }
        if (o4 == 0.0 && onSegment(q1, p2, q2)) {
            return true;
        }

        return false;
    }

    private double orientation(Point2 a, Point2 b, Point2 c) {
        return (b.x() - a.x()) * (c.y() - a.y()) -
                (b.y() - a.y()) * (c.x() - a.x());
    }

    private boolean onSegment(Point2 a, Point2 b, Point2 c) {
        return b.x() >= Math.min(a.x(), c.x()) &&
                b.x() <= Math.max(a.x(), c.x()) &&
                b.y() >= Math.min(a.y(), c.y()) &&
                b.y() <= Math.max(a.y(), c.y());
    }

    static record PreparedLoop(
            List<Point3> boundaryPoints,
            PolygonLoop2 polygonLoop
    ) {}

    PreparedLoop prepareProjectedPolygonLoop(LoopGeom loop, PlaneSurface3 plane) {
        List<List<Point3>> discretizedEdgePointLists = collectDiscretizedEdgePoints(loop);
        List<Point3> boundaryPoints = flattenDiscretizedEdgePointLists(discretizedEdgePointLists);
        List<Point3> cleanedBoundaryPoints = collapseConsecutiveDuplicateBoundaryPoints(boundaryPoints);
        List<Point3> openBoundaryPoints = removeClosingDuplicateBoundaryPoint(cleanedBoundaryPoints);

        validateBoundaryHasAtLeastThreePoints(openBoundaryPoints);

        List<Point2> projectedBoundaryPoints = projectBoundaryPointsTo2D(plane, openBoundaryPoints);
        PolygonLoop2 polygonLoop = buildOuterPolygonLoop(projectedBoundaryPoints);
        validateProjectedBoundaryIsSimple(polygonLoop);

        return new PreparedLoop(openBoundaryPoints, polygonLoop);
    }

    List<PreparedLoop> prepareProjectedPolygonLoops(List<LoopGeom> bounds, PlaneSurface3 plane) {
        if (bounds == null || bounds.isEmpty()) {
            throw new IllegalArgumentException("Face must have at least one bound.");
        }

        List<PreparedLoop> preparedLoops = new ArrayList<>();

        for (LoopGeom bound : bounds) {
            preparedLoops.add(prepareProjectedPolygonLoop(bound, plane));
        }

        return preparedLoops;
    }



    List<Point3> collectPreparedBoundaryPoints(List<PreparedLoop> preparedLoops) {
        List<Point3> boundaryPoints = new ArrayList<>();

        if (preparedLoops == null || preparedLoops.isEmpty()) {
            return boundaryPoints;
        }

        for (PreparedLoop preparedLoop : preparedLoops) {
            if (preparedLoop != null && preparedLoop.boundaryPoints() != null) {
                boundaryPoints.addAll(preparedLoop.boundaryPoints());
            }
        }

        return boundaryPoints;
    }

    List<Point2> collectPreparedProjectedPoints(List<PreparedLoop> preparedLoops) {
        List<Point2> projectedPoints = new ArrayList<>();

        if (preparedLoops == null || preparedLoops.isEmpty()) {
            return projectedPoints;
        }

        for (PreparedLoop preparedLoop : preparedLoops) {
            if (preparedLoop != null &&
                    preparedLoop.polygonLoop() != null &&
                    preparedLoop.polygonLoop().points() != null) {
                projectedPoints.addAll(preparedLoop.polygonLoop().points());
            }
        }

        return projectedPoints;
    }

    PolygonWithHoles2 buildPolygonWithHoles(List<PreparedLoop> preparedLoops) {
        if (preparedLoops == null || preparedLoops.isEmpty()) {
            throw new IllegalArgumentException("Prepared loops must contain at least one loop.");
        }

        PolygonLoop2 outerLoop = preparedLoops.getFirst().polygonLoop();
        List<PolygonLoop2> holes = new ArrayList<>();

        for (int i = 1; i < preparedLoops.size(); i++) {
            holes.add(preparedLoops.get(i).polygonLoop());
        }

        return new PolygonWithHoles2(outerLoop, holes);
    }

    void validateHoleRelationships(PolygonWithHoles2 polygon) {
        if (polygon == null) {
            throw new IllegalArgumentException("Polygon must not be null.");
        }
        if (polygon.outer() == null) {
            throw new IllegalArgumentException("Polygon outer loop must not be null.");
        }
        if (polygon.holes() == null) {
            throw new IllegalArgumentException("Polygon holes must not be null.");
        }

        PolygonLoop2 outer = polygon.outer();
        List<PolygonLoop2> holes = polygon.holes();

        for (PolygonLoop2 hole : holes) {
            validateLoopsDoNotIntersect(outer, hole, "Hole loop must not intersect outer loop.");
            validateHoleInsideOuter(outer, hole);
        }

        for (int i = 0; i < holes.size(); i++) {
            for (int j = i + 1; j < holes.size(); j++) {
                PolygonLoop2 firstHole = holes.get(i);
                PolygonLoop2 secondHole = holes.get(j);

                validateLoopsDoNotIntersect(
                        firstHole,
                        secondHole,
                        "Hole loops must not intersect each other."
                );

                validateHolesDoNotContainEachOther(firstHole, secondHole);
            }
        }
    }

    void validateHolesDoNotContainEachOther(PolygonLoop2 first, PolygonLoop2 second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Polygon loops must not be null.");
        }
        if (first.points() == null || second.points() == null) {
            throw new IllegalArgumentException("Polygon loop points must not be null.");
        }
        if (first.points().isEmpty() || second.points().isEmpty()) {
            throw new IllegalArgumentException("Polygon loop points must not be empty.");
        }

        Point2 firstSample = first.points().getFirst();
        Point2 secondSample = second.points().getFirst();

        if (isPointStrictlyInsidePolygon(firstSample, second) ||
                isPointStrictlyInsidePolygon(secondSample, first)) {
            throw new IllegalArgumentException("Hole loops must not contain each other.");
        }
    }

    void validateHoleInsideOuter(PolygonLoop2 outer, PolygonLoop2 hole) {
        if (outer == null) {
            throw new IllegalArgumentException("Outer loop must not be null.");
        }
        if (hole == null) {
            throw new IllegalArgumentException("Hole loop must not be null.");
        }
        if (outer.points() == null) {
            throw new IllegalArgumentException("Outer loop points must not be null.");
        }
        if (hole.points() == null) {
            throw new IllegalArgumentException("Hole loop points must not be null.");
        }
        if (hole.points().isEmpty()) {
            throw new IllegalArgumentException("Hole loop points must not be empty.");
        }

        for (Point2 holePoint : hole.points()) {
            if (!isPointStrictlyInsidePolygon(holePoint, outer)) {
                throw new IllegalArgumentException("Hole loop must lie inside outer loop.");
            }
        }
    }

    void validateLoopsDoNotIntersect(PolygonLoop2 first, PolygonLoop2 second, String message) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Polygon loops must not be null.");
        }
        if (first.points() == null || second.points() == null) {
            throw new IllegalArgumentException("Polygon loop points must not be null.");
        }

        List<Point2> firstPoints = first.points();
        List<Point2> secondPoints = second.points();

        int firstSegmentCount = firstPoints.size();
        int secondSegmentCount = secondPoints.size();

        for (int i = 0; i < firstSegmentCount; i++) {
            Point2 a1 = firstPoints.get(i);
            Point2 a2 = firstPoints.get((i + 1) % firstSegmentCount);

            for (int j = 0; j < secondSegmentCount; j++) {
                Point2 b1 = secondPoints.get(j);
                Point2 b2 = secondPoints.get((j + 1) % secondSegmentCount);

                if (segmentsIntersect(a1, a2, b1, b2)) {
                    throw new IllegalArgumentException(message);
                }
            }
        }
    }

    boolean isPointStrictlyInsidePolygon(Point2 point, PolygonLoop2 polygonLoop) {
        if (point == null) {
            throw new IllegalArgumentException("Point must not be null.");
        }
        if (polygonLoop == null) {
            throw new IllegalArgumentException("Polygon loop must not be null.");
        }
        if (polygonLoop.points() == null) {
            throw new IllegalArgumentException("Polygon loop points must not be null.");
        }

        List<Point2> points = polygonLoop.points();
        int count = points.size();

        if (count < 3) {
            return false;
        }

        for (int i = 0, j = count - 1; i < count; j = i++) {
            Point2 a = points.get(j);
            Point2 b = points.get(i);

            if (orientation(a, b, point) == 0.0 && onSegment(a, point, b)) {
                return false;
            }
        }

        boolean inside = false;

        for (int i = 0, j = count - 1; i < count; j = i++) {
            Point2 pi = points.get(i);
            Point2 pj = points.get(j);

            boolean intersects = ((pi.y() > point.y()) != (pj.y() > point.y())) &&
                    (point.x() < (pj.x() - pi.x()) * (point.y() - pi.y()) / (pj.y() - pi.y()) + pi.x());

            if (intersects) {
                inside = !inside;
            }
        }

        return inside;
    }
}