package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.*;
import be.doebi.aerismill.tessellation.projection.PlaneProjector;
import be.doebi.aerismill.ui.AppConsole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlanarFaceTessellator implements FaceTessellator {

    private final EdgeDiscretizer edgeDiscretizer;
    private final PolygonTriangulator polygonTriangulator;
    private final PlaneProjector planeProjector;
    private final GeometryTolerance tolerance;

    static int  timesPrintedA = 0;
    static int  timesPrintedB = 0;



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
            throw new IllegalArgumentException(faceLabel(face) + ": only planar faces are supported for now.");
        }
        if (face.bounds() == null || face.bounds().isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": face must have at least one bound.");
        }

        List<PreparedLoop> preparedLoops = prepareProjectedPolygonLoops(face, plane);
        preparedLoops = reorderPreparedLoopsOuterFirst(preparedLoops);

        if ("#26867".equals(face.stepId())) {
            AppConsole.log("PLANAR26867_LOOP_COUNT " + preparedLoops.size());
            for (int i = 0; i < preparedLoops.size(); i++) {
                List<Point2> pts = preparedLoops.get(i).polygonLoop().points();
                String first = pts.isEmpty() ? "[]" : pts.get(0).toString();
                String last = pts.isEmpty() ? "[]" : pts.get(pts.size() - 1).toString();

                AppConsole.log(
                        "PLANAR26867_LOOP " + i
                                + " | count=" + pts.size()
                                + " | area=" + PolygonMath.signedArea(pts)
                                + " | first=" + first
                                + " | last=" + last
                );
            }
        }

        PolygonWithHoles2 polygon = buildPolygonWithHoles(preparedLoops);

        validateHoleRelationships(polygon);

        TriangulationResult triangulation = triangulatePolygon(polygon);

        List<Point2> projectedBoundaryPoints = triangulation.points();
        List<int[]> triangles = triangulation.triangles();

        List<Point2> originalProjectedBoundaryPoints = collectPreparedProjectedPoints(preparedLoops);
        List<Point3> originalBoundaryPoints = collectPreparedBoundaryPoints(preparedLoops);

        List<Point3> boundaryPoints = alignBoundaryPointsToTriangulation(
                originalProjectedBoundaryPoints,
                originalBoundaryPoints,
                projectedBoundaryPoints
        );






        if (preparedLoops.size() == 1) {
            if (originalProjectedBoundaryPoints.size() != projectedBoundaryPoints.size()) {
                AppConsole.log(
                        "PLANAR_ORDER_SIZE_MISMATCH"
                                + " | face=" + face.stepId()
                                + " | originalProjectedCount=" + originalProjectedBoundaryPoints.size()
                                + " | triangulatedProjectedCount=" + projectedBoundaryPoints.size()
                );
            } else {
                boolean exactOrderMatch = true;

                for (int i = 0; i < projectedBoundaryPoints.size(); i++) {
                    Point2 a = originalProjectedBoundaryPoints.get(i);
                    Point2 b = projectedBoundaryPoints.get(i);

                    if (!a.equals(b)) {
                        exactOrderMatch = false;

                        AppConsole.log(
                                "PLANAR_ORDER_MISMATCH"
                                        + " | face=" + face.stepId()
                                        + " | index=" + i
                                        + " | original=" + a
                                        + " | triangulated=" + b
                        );
                        break;
                    }
                }

                if (exactOrderMatch) {
                    // optional success log
                }
            }
        }
















        /*
        AppConsole.log(
                ""

                "PLANAR_TRI_VS_3D"
                        + " | face=" + face.stepId()
                        + " | projectedCount=" + projectedBoundaryPoints.size()
                        + " | boundary3dCount=" + boundaryPoints.size()


        );
        */

        if (projectedBoundaryPoints.size() != boundaryPoints.size()) {
            throw new IllegalArgumentException(
                    "Planar triangulation 2D/3D count mismatch: "
                            + "face=" + face.stepId()
                            + " | projectedCount=" + projectedBoundaryPoints.size()
                            + " | boundary3dCount=" + boundaryPoints.size()
            );
        }











        validateTrianglesNotEmpty(triangles, face, preparedLoops, boundaryPoints);
        validateTriangleIndices(projectedBoundaryPoints, triangles);
        validateTrianglesAreNonDegenerate(triangles);
        validateTrianglesHavePositiveArea(projectedBoundaryPoints, triangles);

        return buildFaceMeshPatch(boundaryPoints, triangles, SurfaceFamily.PLANAR);
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

    List<List<Point3>> collectDiscretizedEdgePoints(LoopGeom bound, FaceGeom face, int boundIndex) {
        if (bound.edges() == null || bound.edges().isEmpty()) {
            throw new IllegalArgumentException(
                    faceBoundLabel(face, boundIndex) + ": face bound must contain at least one edge."
            );
        }

        List<List<Point3>> discretizedEdges = new ArrayList<>();
        boolean foundNonNullEdge = false;

        for (var edge : bound.edges()) {
            if (edge != null) {
                discretizedEdges.add(edgeDiscretizer.discretize(edge, tolerance));
                foundNonNullEdge = true;
            }
        }

        if (!foundNonNullEdge) {
            throw new IllegalArgumentException(
                    faceBoundLabel(face, boundIndex) + ": face bound must contain at least one non-null edge."
            );
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

    List<Point2> projectBoundaryPointsTo2D(
            PlaneSurface3 plane,
            List<Point3> boundaryPoints,
            FaceGeom face,
            int boundIndex
    ) {
        List<Point2> projectedPoints = new ArrayList<>();

        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return projectedPoints;
        }

        for (var point : boundaryPoints) {
            Point2 projected = planeProjector.project(point, plane);
            if (projected == null) {
                throw new IllegalArgumentException(
                        faceBoundLabel(face, boundIndex) + ": projected boundary points must not contain null points."
                );
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
    /*
    List<int[]> triangulatePolygon(PolygonWithHoles2 polygon) {
        if (polygon == null) {
            throw new IllegalArgumentException("Polygon must not be null.");
        }
        return polygonTriangulator.triangulate(polygon);
    }*/
    TriangulationResult triangulatePolygon(PolygonWithHoles2 polygon) {
        if (polygon == null) {
            throw new IllegalArgumentException("Polygon must not be null.");
        }
        return polygonTriangulator.triangulateWithPoints(polygon);
    }

    FaceMeshPatch buildFaceMeshPatch(
            List<Point3> boundaryPoints,
            List<int[]> triangleIndices,
            SurfaceFamily surfaceFamily
    ) {
        if (boundaryPoints == null) {
            throw new IllegalArgumentException("Boundary points must not be null.");
        }
        if (triangleIndices == null) {
            throw new IllegalArgumentException("Triangle indices must not be null.");
        }
        if (surfaceFamily == null) {
            throw new IllegalArgumentException("Surface family must not be null.");
        }

        return new FaceMeshPatch(boundaryPoints, triangleIndices, surfaceFamily);
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

    void validateBoundaryHasAtLeastThreePoints(
            List<Point3> boundaryPoints,
            FaceGeom face,
            int boundIndex,
            BoundPreparationStats stats,
            String stage
    ) {
        int count = boundaryPoints == null ? 0 : boundaryPoints.size();

        if (count < 3) {
            StringBuilder message = new StringBuilder();
            message.append(faceBoundLabel(face, boundIndex))
                    .append(": boundary has only ").append(count)
                    .append(" point(s) at stage '").append(stage)
                    .append("'; at least 3 required for triangulation")
                    .append(" (").append(formatBoundPreparationStats(stats)).append("). ")
                    .append(formatBoundPreparationPointTraces(stats));

            if (stats.frameDebug() != null) {
                message.append(". ").append(stats.frameDebug());
            }

            message.append(".");

            throw new IllegalArgumentException(message.toString());
        }
    }

    void validateTrianglesNotEmpty(
            List<int[]> triangleIndices,
            FaceGeom face,
            List<PreparedLoop> preparedLoops,
            List<Point3> boundaryPoints
    ) {
        if (triangleIndices == null || triangleIndices.isEmpty()) {
            int loopCount = preparedLoops == null ? 0 : preparedLoops.size();
            int boundaryPointCount = boundaryPoints == null ? 0 : boundaryPoints.size();

            throw new IllegalArgumentException(
                    faceLabel(face)
                            + ": triangulation produced no triangles"
                            + " (prepared loops=" + loopCount
                            + ", total boundary points=" + boundaryPointCount + ")."
            );
        }
    }

    void validateTriangleIndices(List<Point2> boundaryPoints, List<int[]> triangleIndices) {
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
                    throw new IllegalArgumentException(
                            "Triangle index out of bounds for boundary vertices: "
                                    + "triangle=[" + triangle[0] + ", " + triangle[1] + ", " + triangle[2] + "]"
                                    + ", offendingIndex=" + index
                                    + ", boundaryVertexCount=" + vertexCount
                    );
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

        int vertexCount = projectedBoundaryPoints.size();

        for (int[] triangle : triangleIndices) {
            if (triangle == null || triangle.length != 3) {
                throw new IllegalArgumentException("Each triangle must contain exactly three vertex indices.");
            }

            for (int index : triangle) {
                if (index < 0 || index >= vertexCount) {
                    throw new IllegalArgumentException("Triangle index out of bounds for projected boundary vertices.");
                }
            }

            Point2 a = projectedBoundaryPoints.get(triangle[0]);
            Point2 b = projectedBoundaryPoints.get(triangle[1]);
            Point2 c = projectedBoundaryPoints.get(triangle[2]);

            double twiceSignedArea = PolygonMath.twiceSignedArea(a, b, c);

            double areaEpsilon = 1e-9;


            if (Math.abs(twiceSignedArea) <= tolerance.pointEqualityEpsilon() * 10.0) {

                int ia = triangle[0];
                int ib = triangle[1];
                int ic = triangle[2];

                Point2 prevA = projectedBoundaryPoints.get((ia - 1 + vertexCount) % vertexCount);
                Point2 nextC = projectedBoundaryPoints.get((ic + 1) % vertexCount);

                /*
                AppConsole.log(
                        ""

                        "DEGEN_CONTEXT "
                                + " tri=" + Arrays.toString(triangle)
                                + " | a=" + a
                                + " b=" + b
                                + " c=" + c
                                + " | prevA=" + prevA
                                + " nextC=" + nextC


                );

                 */
            }





            if (Math.abs(twiceSignedArea) <= areaEpsilon) {
                AppConsole.log("DEGEN_TRI tri=[" + triangle[0] + "," + triangle[1] + "," + triangle[2]
                        + "] area2=" + twiceSignedArea
                        + " a=" + a + " b=" + b + " c=" + c);
                throw new IllegalArgumentException("Triangle must have non-zero area in projected space.");
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

    static record SimplifiedProjectedLoop(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {}

    PreparedLoop prepareProjectedPolygonLoop(FaceGeom face, LoopGeom loop, PlaneSurface3 plane, int boundIndex) {
        List<List<Point3>> discretizedEdgePointLists = collectDiscretizedEdgePoints(loop, face, boundIndex);
        List<Integer> perEdgeSampleCounts = collectEdgeSampleCounts(discretizedEdgePointLists);
        int edgeCount = perEdgeSampleCounts.size();

        List<Point3> boundaryPoints = flattenDiscretizedEdgePointLists(discretizedEdgePointLists);
        int raw3d = boundaryPoints.size();

        List<Point3> cleanedBoundaryPoints = collapseConsecutiveDuplicateBoundaryPoints(boundaryPoints);
        int collapsed3d = cleanedBoundaryPoints.size();

        List<Point3> openBoundaryPoints = removeClosingDuplicateBoundaryPoint(cleanedBoundaryPoints);
        int open3d = openBoundaryPoints.size();

        BoundPreparationStats preProjectionStats = new BoundPreparationStats(
                edgeCount,
                perEdgeSampleCounts,
                raw3d,
                collapsed3d,
                open3d,
                -1,
                -1,
                -1,
                -1,
                -1,
                -1,
                new ArrayList<>(openBoundaryPoints),
                null,
                null,
                null,
                null
        );

        validateBoundaryHasAtLeastThreePoints(
                openBoundaryPoints,
                face,
                boundIndex,
                preProjectionStats,
                "after-3d-cleanup"
        );

        List<Point2> projectedBoundaryPoints = projectBoundaryPointsTo2D(
                plane,
                openBoundaryPoints,
                face,
                boundIndex
        );

        ProjectedCleanupSnapshot projectedSnapshot = snapshotProjectedCleanup(
                openBoundaryPoints,
                projectedBoundaryPoints
        );

        String frameDebug = null;
        if (openBoundaryPoints.size() == 4
                && projectedSnapshot.projectedCollapsed().size() == 3
                && projectedSnapshot.projectedOpen().size() == 2) {
            frameDebug = buildPlaneFrameDebug(plane, openBoundaryPoints);
        }

        BoundPreparationStats postProjectionStats = new BoundPreparationStats(
                edgeCount,
                perEdgeSampleCounts,
                raw3d,
                collapsed3d,
                open3d,
                projectedSnapshot.projectedRaw().size(),
                projectedSnapshot.projectedCollapsed().size(),
                projectedSnapshot.projectedOpen().size(),
                projectedSnapshot.projectedNoBacktracks().size(),
                projectedSnapshot.projectedNoCollinear().size(),
                projectedSnapshot.finalProjected().size(),
                new ArrayList<>(openBoundaryPoints),
                new ArrayList<>(projectedSnapshot.projectedRaw()),
                new ArrayList<>(projectedSnapshot.projectedCollapsed()),
                new ArrayList<>(projectedSnapshot.projectedOpen()),
                frameDebug
        );

        validateBoundaryHasAtLeastThreePoints(
                projectedSnapshot.boundaryPoints(),
                face,
                boundIndex,
                postProjectionStats,
                "after-projected-simplification"
        );


        PolygonLoop2 polygonLoop = buildOuterPolygonLoop(projectedSnapshot.finalProjected());

        polygonLoop = removeRepeatedVertexTouches(polygonLoop);

        if ("#26867".equals(face.stepId())) {
            AppConsole.log("PLANAR26867_BEFORE_VALIDATE count=" + polygonLoop.points().size());

            StringBuilder sb = new StringBuilder("PLANAR26867_INDEXED");
            for (int i = 0; i < polygonLoop.points().size(); i++) {
                sb.append(" | ").append(i).append("=").append(polygonLoop.points().get(i));
            }
            AppConsole.log(sb.toString());
        }






        if ("#26867".equals(face.stepId())) {
            List<Point2> pts = polygonLoop.points();
            double eps = tolerance.pointEqualityEpsilon() * 10.0;

            for (int i = 0; i < pts.size(); i++) {
                for (int j = i + 1; j < pts.size(); j++) {
                    Point2 a = pts.get(i);
                    Point2 b = pts.get(j);

                    boolean same =
                            Math.abs(a.x() - b.x()) <= eps &&
                                    Math.abs(a.y() - b.y()) <= eps;

                    if (same) {
                        AppConsole.log(
                                "PLANAR_REPEAT Face #26867"
                                        + " | i=" + i + " " + a
                                        + " | j=" + j + " " + b
                        );
                    }
                }
            }
        }









        validateProjectedBoundaryIsSimple(polygonLoop, face, boundIndex);

        List<Point3> alignedBoundaryPoints = alignBoundaryPointsToFinalPolygonLoop(
                projectedSnapshot.boundaryPoints(),
                projectedSnapshot.finalProjected(),
                polygonLoop.points()
        );

        return new PreparedLoop(alignedBoundaryPoints, polygonLoop);
    }





    void validateProjectedBoundaryIsSimple(PolygonLoop2 outerLoop, FaceGeom face, int boundIndex) {
        if (outerLoop == null) {
            throw new IllegalArgumentException(
                    faceBoundLabel(face, boundIndex) + ": projected boundary loop must not be null."
            );
        }
        if (outerLoop.points() == null) {
            throw new IllegalArgumentException(
                    faceBoundLabel(face, boundIndex) + ": projected boundary loop points must not be null."
            );
        }

        SegmentIntersection intersection = findSelfIntersection(outerLoop.points());
        if (intersection != null) {
            throw new IllegalArgumentException(
                    faceBoundLabel(face, boundIndex)
                            + ": projected boundary must not self-intersect"
                            + " (segment " + intersection.firstSegmentIndex()
                            + " " + intersection.a1() + " -> " + intersection.a2()
                            + ", segment " + intersection.secondSegmentIndex()
                            + " " + intersection.b1() + " -> " + intersection.b2() + ")."
            );
        }
    }

    List<PreparedLoop> prepareProjectedPolygonLoops(FaceGeom face, PlaneSurface3 plane) {
        List<LoopGeom> bounds = face.bounds();

        if (bounds == null || bounds.isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": face must have at least one bound.");
        }

        List<PreparedLoop> preparedLoops = new ArrayList<>();

        for (int i = 0; i < bounds.size(); i++) {
            preparedLoops.add(prepareProjectedPolygonLoop(face, bounds.get(i), plane, i));
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

    private String faceLabel(FaceGeom face) {
        if (face == null || face.stepId() == null || face.stepId().isBlank()) {
            return "Face <unknown>";
        }
        return "Face " + face.stepId();
    }

    private String faceBoundLabel(FaceGeom face, int boundIndex) {
        return faceLabel(face) + ", bound " + (boundIndex + 1);
    }

    SimplifiedProjectedLoop simplifyProjectedLoop(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        if (boundaryPoints == null) {
            throw new IllegalArgumentException("Boundary points must not be null.");
        }
        if (projectedPoints == null) {
            throw new IllegalArgumentException("Projected points must not be null.");
        }
        if (boundaryPoints.size() != projectedPoints.size()) {
            throw new IllegalArgumentException("Boundary points and projected points must have the same size.");
        }

        List<Point3> currentBoundary = new ArrayList<>(boundaryPoints);
        List<Point2> currentProjected = new ArrayList<>(projectedPoints);

        boolean changed;
        do {
            changed = false;

            SimplifiedProjectedLoop collapsed = collapseConsecutiveDuplicateProjectedPoints(
                    currentBoundary,
                    currentProjected
            );
            if (collapsed.projectedPoints().size() != currentProjected.size()) {
                changed = true;
            }

            SimplifiedProjectedLoop withoutClosingDuplicate = removeClosingDuplicateProjectedPoint(
                    collapsed.boundaryPoints(),
                    collapsed.projectedPoints()
            );
            if (withoutClosingDuplicate.projectedPoints().size() != collapsed.projectedPoints().size()) {
                changed = true;
            }

            SimplifiedProjectedLoop withoutBacktracks = removeImmediateBacktracks(
                    withoutClosingDuplicate.boundaryPoints(),
                    withoutClosingDuplicate.projectedPoints()
            );
            if (withoutBacktracks.projectedPoints().size() != withoutClosingDuplicate.projectedPoints().size()) {
                changed = true;
            }

            SimplifiedProjectedLoop withoutCollinearPoints = removeCollinearProjectedPoints(
                    withoutBacktracks.boundaryPoints(),
                    withoutBacktracks.projectedPoints()
            );
            if (withoutCollinearPoints.projectedPoints().size() != withoutBacktracks.projectedPoints().size()) {
                changed = true;
            }

            currentBoundary = withoutCollinearPoints.boundaryPoints();
            currentProjected = withoutCollinearPoints.projectedPoints();

        } while (changed);

        return new SimplifiedProjectedLoop(currentBoundary, currentProjected);
    }


    List<Point2> collapseConsecutiveDuplicateProjectedPoints(List<Point2> points) {
        List<Point2> collapsed = new ArrayList<>();

        if (points == null || points.isEmpty()) {
            return collapsed;
        }

        Point2 previous = null;

        for (Point2 point : points) {
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

    SimplifiedProjectedLoop removeClosingDuplicateProjectedPoint(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        List<Point3> resultBoundary = new ArrayList<>();
        List<Point2> resultProjected = new ArrayList<>();

        if (boundaryPoints == null || projectedPoints == null || boundaryPoints.isEmpty()) {
            return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
        }

        resultBoundary.addAll(boundaryPoints);
        resultProjected.addAll(projectedPoints);

        if (resultProjected.size() > 1 && resultProjected.getFirst().equals(resultProjected.getLast())) {
            resultBoundary.removeLast();
            resultProjected.removeLast();
        }

        return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
    }

    List<Point2> removeImmediateBacktracks(List<Point2> points) {
        List<Point2> result = new ArrayList<>();

        if (points == null || points.isEmpty()) {
            return result;
        }

        for (Point2 point : points) {
            result.add(point);

            while (result.size() >= 3) {
                int size = result.size();

                Point2 a = result.get(size - 3);
                Point2 b = result.get(size - 2);
                Point2 c = result.get(size - 1);

                if (a.equals(c)) {
                    result.remove(size - 1);
                    result.remove(size - 2);
                } else {
                    break;
                }
            }
        }

        return result;
    }

    List<Point2> removeCollinearProjectedPoints(List<Point2> points) {
        List<Point2> result = new ArrayList<>();

        if (points == null || points.size() < 3) {
            if (points != null) {
                result.addAll(points);
            }
            return result;
        }

        int size = points.size();

        for (int i = 0; i < size; i++) {
            Point2 previous = points.get((i - 1 + size) % size);
            Point2 current = points.get(i);
            Point2 next = points.get((i + 1) % size);

            if (orientation(previous, current, next) == 0.0 && onSegment(previous, current, next)) {
                continue;
            }

            result.add(current);
        }

        return result;
    }

    SimplifiedProjectedLoop collapseConsecutiveDuplicateProjectedPoints(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        List<Point3> collapsedBoundary = new ArrayList<>();
        List<Point2> collapsedProjected = new ArrayList<>();

        if (boundaryPoints == null || projectedPoints == null || boundaryPoints.isEmpty()) {
            return new SimplifiedProjectedLoop(collapsedBoundary, collapsedProjected);
        }

        Point2 previous = null;

        for (int i = 0; i < projectedPoints.size(); i++) {
            Point2 projectedPoint = projectedPoints.get(i);
            Point3 boundaryPoint = boundaryPoints.get(i);

            if (projectedPoint == null || boundaryPoint == null) {
                continue;
            }

            if (!projectedPoint.equals(previous)) {
                collapsedBoundary.add(boundaryPoint);
                collapsedProjected.add(projectedPoint);
                previous = projectedPoint;
            }
        }

        return new SimplifiedProjectedLoop(collapsedBoundary, collapsedProjected);
    }



    SimplifiedProjectedLoop removeImmediateBacktracks(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        List<Point3> resultBoundary = new ArrayList<>();
        List<Point2> resultProjected = new ArrayList<>();

        if (boundaryPoints == null || projectedPoints == null || boundaryPoints.isEmpty()) {
            return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
        }

        for (int i = 0; i < projectedPoints.size(); i++) {
            resultBoundary.add(boundaryPoints.get(i));
            resultProjected.add(projectedPoints.get(i));

            while (resultProjected.size() >= 3) {
                int size = resultProjected.size();

                Point2 a = resultProjected.get(size - 3);
                Point2 c = resultProjected.get(size - 1);

                if (a.equals(c)) {
                    resultProjected.remove(size - 1);
                    resultProjected.remove(size - 2);

                    resultBoundary.remove(size - 1);
                    resultBoundary.remove(size - 2);
                } else {
                    break;
                }
            }
        }

        return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
    }

    /*
    SimplifiedProjectedLoop removeCollinearProjectedPoints(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        List<Point3> resultBoundary = new ArrayList<>();
        List<Point2> resultProjected = new ArrayList<>();

        if (boundaryPoints == null || projectedPoints == null) {
            return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
        }

        if (projectedPoints.size() < 3) {
            resultBoundary.addAll(boundaryPoints);
            resultProjected.addAll(projectedPoints);
            return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
        }

        int size = projectedPoints.size();

        for (int i = 0; i < size; i++) {
            Point2 previous = projectedPoints.get((i - 1 + size) % size);
            Point2 current = projectedPoints.get(i);
            Point2 next = projectedPoints.get((i + 1) % size);

            if (orientation(previous, current, next) == 0.0 && onSegment(previous, current, next)) {
                continue;
            }

            resultBoundary.add(boundaryPoints.get(i));
            resultProjected.add(current);
        }

        return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
    }

     */
    SimplifiedProjectedLoop removeCollinearProjectedPoints(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        List<Point3> resultBoundary = new ArrayList<>();
        List<Point2> resultProjected = new ArrayList<>();

        if (boundaryPoints == null || projectedPoints == null) {
            return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
        }

        // Do not simplify triangles or smaller loops here.
        // Let later validation stages handle degenerate 3-point cases.
        if (projectedPoints.size() <= 3) {
            resultBoundary.addAll(boundaryPoints);
            resultProjected.addAll(projectedPoints);
            return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
        }

        int size = projectedPoints.size();

        for (int i = 0; i < size; i++) {
            Point2 previous = projectedPoints.get((i - 1 + size) % size);
            Point2 current = projectedPoints.get(i);
            Point2 next = projectedPoints.get((i + 1) % size);

            if (orientation(previous, current, next) == 0.0 && onSegment(previous, current, next)) {
                continue;
            }

            resultBoundary.add(boundaryPoints.get(i));
            resultProjected.add(current);
        }

        // If simplification would collapse the loop below 3 points,
        // keep the original and let later validation report the real issue.
        if (resultProjected.size() < 3) {
            return new SimplifiedProjectedLoop(
                    new ArrayList<>(boundaryPoints),
                    new ArrayList<>(projectedPoints)
            );
        }

        return new SimplifiedProjectedLoop(resultBoundary, resultProjected);
    }

    private String formatLoopStats(LoopPreparationStats stats) {
        return "raw3d=" + stats.raw3dCount()
                + ", collapsed3d=" + stats.collapsed3dCount()
                + ", open3d=" + stats.open3dCount()
                + ", projected2d=" + stats.projected2dCount()
                + ", simplified2d=" + stats.simplified2dCount();
    }



    private void appendStat(StringBuilder sb, String label, int value) {
        if (value < 0) {
            return;
        }
        if (!sb.isEmpty()) {
            sb.append(", ");
        }
        sb.append(label).append("=").append(value);
    }

    void validateBoundaryHasAtLeastThreePoints(
            List<Point3> boundaryPoints,
            FaceGeom face,
            int boundIndex,
            LoopPreparationStats stats,
            String stage
    ) {
        int count = boundaryPoints == null ? 0 : boundaryPoints.size();

        if (count < 3) {
            throw new IllegalArgumentException(
                    faceBoundLabel(face, boundIndex)
                            + ": boundary has only " + count
                            + " point(s) at stage '" + stage + "'; at least 3 required for triangulation"
                            + " (" + formatLoopStats(stats) + ")."
            );
        }
    }

    SegmentIntersection findSelfIntersection(List<Point2> points) {
        if (points == null || points.size() < 4) {
            return null;
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
                    return new SegmentIntersection(i, j, a1, a2, b1, b2);
                }
            }
        }

        return null;
    }

    List<Integer> collectEdgeSampleCounts(List<List<Point3>> discretizedEdgePointLists) {
        List<Integer> counts = new ArrayList<>();

        if (discretizedEdgePointLists == null) {
            return counts;
        }

        for (List<Point3> edgePoints : discretizedEdgePointLists) {
            counts.add(edgePoints == null ? 0 : edgePoints.size());
        }

        return counts;
    }

    ProjectedCleanupSnapshot snapshotProjectedCleanup(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        if (boundaryPoints == null) {
            throw new IllegalArgumentException("Boundary points must not be null.");
        }
        if (projectedPoints == null) {
            throw new IllegalArgumentException("Projected points must not be null.");
        }
        if (boundaryPoints.size() != projectedPoints.size()) {
            throw new IllegalArgumentException("Boundary points and projected points must have the same size.");
        }

        List<Point2> projectedRaw = new ArrayList<>(projectedPoints);

        SimplifiedProjectedLoop collapsed = collapseConsecutiveDuplicateProjectedPoints(
                boundaryPoints,
                projectedPoints
        );

        SimplifiedProjectedLoop withoutClosingDuplicate = removeClosingDuplicateProjectedPoint(
                collapsed.boundaryPoints(),
                collapsed.projectedPoints()
        );

        SimplifiedProjectedLoop withoutBacktracks = removeImmediateBacktracks(
                withoutClosingDuplicate.boundaryPoints(),
                withoutClosingDuplicate.projectedPoints()
        );

        SimplifiedProjectedLoop withoutCollinear = removeCollinearProjectedPoints(
                withoutBacktracks.boundaryPoints(),
                withoutBacktracks.projectedPoints()
        );

        SimplifiedProjectedLoop finalLoop = simplifyProjectedLoop(
                boundaryPoints,
                projectedPoints
        );

        return new ProjectedCleanupSnapshot(
                finalLoop.boundaryPoints(),
                projectedRaw,
                new ArrayList<>(collapsed.projectedPoints()),
                new ArrayList<>(withoutClosingDuplicate.projectedPoints()),
                new ArrayList<>(withoutBacktracks.projectedPoints()),
                new ArrayList<>(withoutCollinear.projectedPoints()),
                new ArrayList<>(finalLoop.projectedPoints())
        );
    }

    private String formatBoundPreparationStats(BoundPreparationStats stats) {
        return "edgeCount=" + stats.edgeCount()
                + ", perEdgeSampleCounts=" + stats.perEdgeSampleCounts()
                + ", raw3d=" + stats.raw3d()
                + ", collapsed3d=" + stats.collapsed3d()
                + ", open3d=" + stats.open3d()
                + ", projectedRaw=" + stats.projectedRaw()
                + ", projectedCollapsed=" + stats.projectedCollapsed()
                + ", projectedOpen=" + stats.projectedOpen()
                + ", projectedNoBacktracks=" + stats.projectedNoBacktracks()
                + ", projectedNoCollinear=" + stats.projectedNoCollinear()
                + ", final2d=" + stats.final2d();
    }



    private String formatBoundPreparationPointTraces(BoundPreparationStats stats) {
        return "open3dPoints=" + formatPoint3List(stats.open3dPoints())
                + ", projectedRawPoints=" + formatPoint2List(stats.projectedRawPoints())
                + ", projectedCollapsedPoints=" + formatPoint2List(stats.projectedCollapsedPoints())
                + ", projectedOpenPoints=" + formatPoint2List(stats.projectedOpenPoints());
    }

    private String formatPoint3List(List<Point3> points) {
        if (points == null) {
            return "null";
        }
        if (points.isEmpty()) {
            return "[]";
        }

        int limit = Math.min(points.size(), 8);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Point3 p = points.get(i);
            sb.append("(").append(p.x()).append(", ").append(p.y()).append(", ").append(p.z()).append(")");
        }
        if (points.size() > limit) {
            sb.append(", ... total=").append(points.size());
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatPoint2List(List<Point2> points) {
        if (points == null) {
            return "null";
        }
        if (points.isEmpty()) {
            return "[]";
        }

        int limit = Math.min(points.size(), 8);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Point2 p = points.get(i);
            sb.append("(").append(p.x()).append(", ").append(p.y()).append(")");
        }
        if (points.size() > limit) {
            sb.append(", ... total=").append(points.size());
        }
        sb.append("]");
        return sb.toString();
    }

    private void debugPlaneProjectionBasis(
            FaceGeom face,
            int boundIndex,
            PlaneSurface3 plane,
            List<Point3> open3dPoints,
            List<Point2> projectedRawPoints
    ) {
        System.out.println(
                faceBoundLabel(face, boundIndex)
                        + " projection debug:"
                        + " origin=" + plane.frame().origin()
                        + ", xAxis=" + plane.frame().xAxis()
                        + ", yAxis=" + plane.frame().yAxis()
                        + ", open3dPoints=" + open3dPoints
                        + ", projectedRawPoints=" + projectedRawPoints
        );
    }

    private void debugPlaneFrameCoordinates(
            FaceGeom face,
            int boundIndex,
            PlaneSurface3 plane,
            List<Point3> open3dPoints
    ) {
        Point3 origin = plane.frame().origin();
        Vec3 xAxis = plane.frame().xAxis().toVec3();
        Vec3 yAxis = plane.frame().yAxis().toVec3();
        Vec3 zAxis = plane.frame().zAxis().toVec3();

        System.out.println(faceBoundLabel(face, boundIndex) + " frame debug:"
                + " origin=" + origin
                + ", xAxis=" + plane.frame().xAxis()
                + ", yAxis=" + plane.frame().yAxis()
                + ", zAxis=" + plane.frame().zAxis());

        for (int i = 0; i < open3dPoints.size(); i++) {
            Point3 p = open3dPoints.get(i);
            Vec3 delta = p.subtract(origin);

            double dx = delta.dot(xAxis);
            double dy = delta.dot(yAxis);
            double dz = delta.dot(zAxis);

            System.out.println("  p" + i + "=" + p
                    + " -> local=(" + dx + ", " + dy + ", " + dz + ")");
        }
    }

    private String buildPlaneFrameDebug(
            PlaneSurface3 plane,
            List<Point3> open3dPoints
    ) {
        Point3 origin = plane.frame().origin();
        Vec3 xAxis = plane.frame().xAxis().toVec3();
        Vec3 yAxis = plane.frame().yAxis().toVec3();
        Vec3 zAxis = plane.frame().zAxis().toVec3();

        StringBuilder sb = new StringBuilder();
        sb.append("frameOrigin=").append(origin)
                .append(", frameXAxis=").append(plane.frame().xAxis())
                .append(", frameYAxis=").append(plane.frame().yAxis())
                .append(", frameZAxis=").append(plane.frame().zAxis())
                .append(", localPoints=[");

        for (int i = 0; i < open3dPoints.size(); i++) {
            Point3 p = open3dPoints.get(i);
            Vec3 delta = p.subtract(origin);

            double dx = delta.dot(xAxis);
            double dy = delta.dot(yAxis);
            double dz = delta.dot(zAxis);

            if (i > 0) {
                sb.append(", ");
            }

            sb.append("p").append(i)
                    .append("=(").append(dx).append(", ").append(dy).append(", ").append(dz).append(")");
        }

        sb.append("]");
        return sb.toString();
    }

    static record LoopPreparationStats(
            int raw3dCount,
            int collapsed3dCount,
            int open3dCount,
            int projected2dCount,
            int simplified2dCount
    ) {}

    static record SegmentIntersection(
            int firstSegmentIndex,
            int secondSegmentIndex,
            Point2 a1,
            Point2 a2,
            Point2 b1,
            Point2 b2
    ) {}

    static record BoundPreparationStats(
            int edgeCount,
            List<Integer> perEdgeSampleCounts,
            int raw3d,
            int collapsed3d,
            int open3d,
            int projectedRaw,
            int projectedCollapsed,
            int projectedOpen,
            int projectedNoBacktracks,
            int projectedNoCollinear,
            int final2d,
            List<Point3> open3dPoints,
            List<Point2> projectedRawPoints,
            List<Point2> projectedCollapsedPoints,
            List<Point2> projectedOpenPoints,
            String frameDebug
    ) {}

    static record ProjectedCleanupSnapshot(
            List<Point3> boundaryPoints,
            List<Point2> projectedRaw,
            List<Point2> projectedCollapsed,
            List<Point2> projectedOpen,
            List<Point2> projectedNoBacktracks,
            List<Point2> projectedNoCollinear,
            List<Point2> finalProjected
    ) {}

    private PolygonLoop2 removeRepeatedVertexTouches(PolygonLoop2 loop) {
        if (loop == null || loop.points() == null || loop.points().isEmpty()) {
            return loop;
        }

        List<Point2> points = loop.points();
        double eps = tolerance.pointEqualityEpsilon() * 10.0;

        List<Point2> filtered = new ArrayList<>();
        List<Integer> removedIndices = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            Point2 curr = points.get(i);

            boolean alreadySeen = false;
            for (Point2 kept : filtered) {
                boolean same =
                        Math.abs(curr.x() - kept.x()) <= eps &&
                                Math.abs(curr.y() - kept.y()) <= eps;

                if (same) {
                    alreadySeen = true;
                    break;
                }
            }

            if (alreadySeen) {
                removedIndices.add(i);
                continue;
            }

            filtered.add(curr);
        }

        if (!removedIndices.isEmpty()) {
            AppConsole.log(
                    "PLANAR_REPEAT_REMOVED count=" + removedIndices.size()
                            + " | removedIndices=" + removedIndices
            );
        }

        if (filtered.size() < 3) {
            return loop;
        }

        return new PolygonLoop2(filtered);
    }

    private List<PreparedLoop> reorderPreparedLoopsOuterFirst(List<PreparedLoop> preparedLoops) {
        if (preparedLoops == null || preparedLoops.size() <= 1) {
            return preparedLoops;
        }

        List<PreparedLoop> reordered = new ArrayList<>(preparedLoops);

        int outerIndex = 0;
        double maxAbsArea = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < reordered.size(); i++) {
            List<Point2> pts = reordered.get(i).polygonLoop().points();
            double absArea = Math.abs(PolygonMath.signedArea(pts));
            if (absArea > maxAbsArea) {
                maxAbsArea = absArea;
                outerIndex = i;
            }
        }

        if (outerIndex == 0) {
            return reordered;
        }

        PreparedLoop outer = reordered.remove(outerIndex);
        reordered.addFirst(outer);
        return reordered;
    }


    public record TriangulationResult(
            List<Point2> points,
            //List<Integer> sourceIndices,
            List<int[]> triangles
    ) {}



    private List<Point3> alignBoundaryPointsToTriangulation(
            List<Point2> originalProjectedPoints,
            List<Point3> originalBoundaryPoints,
            List<Point2> triangulatedProjectedPoints
    ) {
        if (originalProjectedPoints == null) {
            throw new IllegalArgumentException("Original projected points must not be null.");
        }
        if (originalBoundaryPoints == null) {
            throw new IllegalArgumentException("Original boundary points must not be null.");
        }
        if (triangulatedProjectedPoints == null) {
            throw new IllegalArgumentException("Triangulated projected points must not be null.");
        }
        if (originalProjectedPoints.size() != originalBoundaryPoints.size()) {
            throw new IllegalArgumentException(
                    "Original projected/3D boundary point count mismatch: "
                            + originalProjectedPoints.size() + " vs " + originalBoundaryPoints.size()
            );
        }

        List<Point3> aligned = new ArrayList<>(triangulatedProjectedPoints.size());
        boolean[] used = new boolean[originalProjectedPoints.size()];

        for (Point2 triangulatedPoint : triangulatedProjectedPoints) {
            int matchIndex = -1;

            // First try to consume an unused exact match.
            for (int i = 0; i < originalProjectedPoints.size(); i++) {
                if (used[i]) {
                    continue;
                }
                if (originalProjectedPoints.get(i).equals(triangulatedPoint)) {
                    matchIndex = i;
                    used[i] = true;
                    break;
                }
            }

            // If none left, allow reuse of an existing exact match.
            // This is what we want for duplicated bridge vertices.
            if (matchIndex < 0) {
                for (int i = 0; i < originalProjectedPoints.size(); i++) {
                    if (originalProjectedPoints.get(i).equals(triangulatedPoint)) {
                        matchIndex = i;
                        break;
                    }
                }
            }

            if (matchIndex < 0) {
                throw new IllegalArgumentException(
                        "Could not align triangulated projected point back to 3D boundary point: "
                                + triangulatedPoint
                );
            }

            aligned.add(originalBoundaryPoints.get(matchIndex));
        }

        return aligned;
    }

    private List<Point3> alignBoundaryPointsToFinalPolygonLoop(
            List<Point3> originalBoundaryPoints,
            List<Point2> originalProjectedPoints,
            List<Point2> finalProjectedPoints
    ) {
        if (originalBoundaryPoints == null) {
            throw new IllegalArgumentException("Original boundary points must not be null.");
        }
        if (originalProjectedPoints == null) {
            throw new IllegalArgumentException("Original projected points must not be null.");
        }
        if (finalProjectedPoints == null) {
            throw new IllegalArgumentException("Final projected points must not be null.");
        }
        if (originalBoundaryPoints.size() != originalProjectedPoints.size()) {
            throw new IllegalArgumentException(
                    "Original projected/3D boundary point count mismatch: "
                            + originalProjectedPoints.size() + " vs " + originalBoundaryPoints.size()
            );
        }

        List<Point3> aligned = new ArrayList<>(finalProjectedPoints.size());
        boolean[] used = new boolean[originalProjectedPoints.size()];

        for (Point2 finalPoint : finalProjectedPoints) {
            int matchIndex = -1;

            for (int i = 0; i < originalProjectedPoints.size(); i++) {
                if (used[i]) {
                    continue;
                }
                if (originalProjectedPoints.get(i).equals(finalPoint)) {
                    matchIndex = i;
                    used[i] = true;
                    break;
                }
            }

            if (matchIndex < 0) {
                for (int i = 0; i < originalProjectedPoints.size(); i++) {
                    if (originalProjectedPoints.get(i).equals(finalPoint)) {
                        matchIndex = i;
                        break;
                    }
                }
            }

            if (matchIndex < 0) {
                throw new IllegalArgumentException(
                        "Could not align final projected point back to 3D boundary point: " + finalPoint
                );
            }

            aligned.add(originalBoundaryPoints.get(matchIndex));
        }

        return aligned;
    }
}