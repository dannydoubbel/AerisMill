package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.CylindricalSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.*;
import be.doebi.aerismill.ui.AppConsole;

import java.util.ArrayList;
import java.util.List;

public final class CylindricalFaceTessellator implements FaceTessellator {

    private final PlanarFaceTessellator shared;
    private final GeometryTolerance tolerance;

    public CylindricalFaceTessellator(
            EdgeDiscretizer edgeDiscretizer,
            PolygonTriangulator polygonTriangulator,
            GeometryTolerance tolerance
    ) {
        this.shared = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                null,
                tolerance
        );
        this.tolerance = tolerance;
    }

    @Override
    public FaceMeshPatch tessellate(FaceGeom face) {
        if (!(face.surface() instanceof CylindricalSurface3 cylinder)) {
            throw new IllegalArgumentException(faceLabel(face) + ": only cylindrical faces are supported here.");
        }
        if (face.bounds() == null || face.bounds().isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": face must have at least one bound.");
        }

        if (face.bounds().size() == 1 && isTwoLineStripLoop(face.bounds().getFirst())) {
            return tessellateTwoLineStrip(face, face.bounds().getFirst());
        }

        List<PlanarFaceTessellator.PreparedLoop> preparedLoops =
                prepareProjectedPolygonLoops(face, cylinder);
        preparedLoops = reorderPreparedLoopsOuterFirst(preparedLoops);

        List<Point3> boundaryPoints = shared.collectPreparedBoundaryPoints(preparedLoops);
        List<Point2> projectedBoundaryPoints = shared.collectPreparedProjectedPoints(preparedLoops);



        try {
            PolygonWithHoles2 polygon = shared.buildPolygonWithHoles(preparedLoops);
            List<int[]> triangles = shared.triangulatePolygon(polygon);


            shared.validateHoleRelationships(polygon);
            shared.validateTrianglesNotEmpty(triangles, face, preparedLoops, boundaryPoints);
            shared.validateTriangleIndices(boundaryPoints, triangles);
            shared.validateTrianglesAreNonDegenerate(triangles);
            shared.validateTrianglesHavePositiveArea(projectedBoundaryPoints, triangles);

            return shared.buildFaceMeshPatch(boundaryPoints, triangles,SurfaceFamily.CYLINDRICAL);
        } catch (IllegalArgumentException ex) {
            AppConsole.log("CYL_CATCH " + faceLabel(face) + " -> " + ex.getMessage());

            StringBuilder sb = new StringBuilder();
            sb.append("CYL_DEBUG ")
                    .append(faceLabel(face))
                    .append(" | loopCount=")
                    .append(preparedLoops.size());



            for (int i = 0; i < preparedLoops.size(); i++) {
                PlanarFaceTessellator.PreparedLoop preparedLoop = preparedLoops.get(i);
                List<Point2> points = preparedLoop == null || preparedLoop.polygonLoop() == null
                        ? List.of()
                        : preparedLoop.polygonLoop().points();

                int count = points.size();
                double minX = count == 0 ? Double.NaN : minX(points);
                double maxX = count == 0 ? Double.NaN : maxX(points);
                double minY = count == 0 ? Double.NaN : minY(points);
                double maxY = count == 0 ? Double.NaN : maxY(points);
                double width = count == 0 ? Double.NaN : (maxX - minX);
                double height = count == 0 ? Double.NaN : (maxY - minY);

                sb.append(" | [").append(i).append("]")
                        .append(" count=").append(count)
                        .append(", x=[").append(format(minX)).append(", ").append(format(maxX)).append("]")
                        .append(", y=[").append(format(minY)).append(", ").append(format(maxY)).append("]")
                        .append(", w=").append(format(width))
                        .append(", h=").append(format(height))
                        .append(", area=").append(format(PolygonMath.signedArea(points)))
                        .append(", orth=").append(orthogonalSegmentStats(points))
                        .append(", sample=").append(samplePoints(points, 10));
            }


            AppConsole.log(sb.toString());

            boolean orthogonalSingleLoop = isOrthogonalSingleLoop(preparedLoops);
            String message = ex.getMessage();

            if (message != null
                    && message.contains("ear clipping")
                    && orthogonalSingleLoop
                    && preparedLoops.size() == 1) {

                AppConsole.log("CYL_ORTHO_FALLBACK " + faceLabel(face));

                List<Point2> points = preparedLoops.getFirst().polygonLoop().points();
                List<int[]> fallbackTriangles = tryOrthogonalSingleLoopFallbackTriangles(points);

                try {
                    shared.validateTrianglesNotEmpty(fallbackTriangles, face, preparedLoops, boundaryPoints);
                    shared.validateTriangleIndices(boundaryPoints, fallbackTriangles);
                    shared.validateTrianglesAreNonDegenerate(fallbackTriangles);
                    shared.validateTrianglesHavePositiveArea(projectedBoundaryPoints, fallbackTriangles);

                    AppConsole.log("CYL_ORTHO_FALLBACK_OK " + faceLabel(face));
                    return shared.buildFaceMeshPatch(boundaryPoints, fallbackTriangles,SurfaceFamily.CYLINDRICAL);
                } catch (IllegalArgumentException fallbackEx) {
                    AppConsole.log("CYL_ORTHO_FALLBACK_FAIL " + faceLabel(face) + " -> " + fallbackEx.getMessage());
                }
            }

            if (message != null
                    && message.contains("ear clipping")
                    && orthogonalSingleLoop) {
                AppConsole.log("CYL_ORTHO_FALLBACK " + faceLabel(face));

            }



            throw ex;
        }
    }

    List<PlanarFaceTessellator.PreparedLoop> prepareProjectedPolygonLoops(
            FaceGeom face,
            CylindricalSurface3 cylinder
    ) {
        List<LoopGeom> bounds = face.bounds();

        if (bounds == null || bounds.isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": face must have at least one bound.");
        }

        List<PlanarFaceTessellator.PreparedLoop> preparedLoops = new ArrayList<>();

        for (int i = 0; i < bounds.size(); i++) {
            PlanarFaceTessellator.PreparedLoop prepared =
                    prepareProjectedPolygonLoop(face, bounds.get(i), cylinder, i);

            if (i > 0) {
                List<Point2> aligned =
                        alignLoopToReferenceBand(prepared.polygonLoop().points(),
                                preparedLoops.getFirst().polygonLoop().points(),
                                cylinder.radius());

                PolygonLoop2 alignedLoop = new PolygonLoop2(aligned);
                shared.validateProjectedBoundaryIsSimple(alignedLoop, face, i);

                prepared = new PlanarFaceTessellator.PreparedLoop(
                        prepared.boundaryPoints(),
                        alignedLoop
                );
            }

            preparedLoops.add(prepared);
        }

        return preparedLoops;
    }


    PlanarFaceTessellator.PreparedLoop prepareProjectedPolygonLoop(
            FaceGeom face,
            LoopGeom loop,
            CylindricalSurface3 cylinder,
            int boundIndex
    ) {
        List<List<Point3>> discretizedEdgePointLists =
                shared.collectDiscretizedEdgePoints(loop, face, boundIndex);


        List<Point3> boundaryPoints =
                shared.flattenDiscretizedEdgePointLists(discretizedEdgePointLists);

        List<Point3> cleanedBoundaryPoints =
                shared.collapseConsecutiveDuplicateBoundaryPoints(boundaryPoints);

        List<Point3> openBoundaryPoints =
                shared.removeClosingDuplicateBoundaryPoint(cleanedBoundaryPoints);

        if (openBoundaryPoints == null || openBoundaryPoints.size() < 3) {
            logBoundaryCollapseDiagnostics(
                    face,
                    boundIndex,
                    discretizedEdgePointLists,
                    boundaryPoints,
                    cleanedBoundaryPoints,
                    openBoundaryPoints
            );

            logBoundaryEdgeDetails(loop, face, boundIndex);

            validateBoundaryHasAtLeastThreePoints(openBoundaryPoints, face, boundIndex, "after-3d-cleanup");
        }

        try {

            List<Point2> projectedBoundaryPoints =
                    projectBoundaryPointsTo2D(cylinder, openBoundaryPoints, face, boundIndex);

            projectedBoundaryPoints =
                    normalizeLoopToCompactBand(projectedBoundaryPoints, cylinder.radius());


            PlanarFaceTessellator.SimplifiedProjectedLoop simplifiedLoop =
                    shared.simplifyProjectedLoop(openBoundaryPoints, projectedBoundaryPoints);

            simplifiedLoop = removeProjectedCollinearPoints(
                    simplifiedLoop.boundaryPoints(),
                    simplifiedLoop.projectedPoints()
            );

            simplifiedLoop = collapseHorizontalPlateaus(
                    face,
                    simplifiedLoop.boundaryPoints(),
                    simplifiedLoop.projectedPoints()
            );






            validateBoundaryHasAtLeastThreePoints(
                    simplifiedLoop.boundaryPoints(),
                    face,
                    boundIndex,
                    "after-cylindrical-projection"
            );

            if ("#84523".equals(face.stepId()) || "#145264".equals(face.stepId()) || "#104057".equals(face.stepId()) || "#67665".equals(face.stepId())) {
                AppConsole.log(
                        "CYL_FINAL "
                                + faceBoundLabel(face, boundIndex)
                                + " | count=" + simplifiedLoop.projectedPoints().size()
                                + " | area=" + format(PolygonMath.signedArea(simplifiedLoop.projectedPoints()))
                                + " | orth=" + orthogonalSegmentStats(simplifiedLoop.projectedPoints())
                                + " | sample=" + samplePoints(simplifiedLoop.projectedPoints(), 20)
                );
            }

            PolygonLoop2 polygonLoop = shared.buildOuterPolygonLoop(simplifiedLoop.projectedPoints());

            try {
                shared.validateProjectedBoundaryIsSimple(polygonLoop, face, boundIndex);
            } catch (IllegalArgumentException ex) {
                AppConsole.log(
                        "CYL_SIMPLE "
                                + faceBoundLabel(face, boundIndex)
                                + " | count=" + polygonLoop.points().size()
                                + " | area=" + format(PolygonMath.signedArea(polygonLoop.points()))
                                + " | sample=" + samplePoints(polygonLoop.points(), 10)
                                + " | msg=" + ex.getMessage()
                );
                throw ex;
            }
            return new PlanarFaceTessellator.PreparedLoop(
                    simplifiedLoop.boundaryPoints(),
                    polygonLoop
            );
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    ex.getMessage() + " | cylindrical-debug: "
                            + summarizeProjectedLoop(openBoundaryPoints, cylinder),
                    ex
            );
        }
        /* ************************* */
    }

    List<Point2> projectBoundaryPointsTo2D(
            CylindricalSurface3 cylinder,
            List<Point3> boundaryPoints,
            FaceGeom face,
            int boundIndex
    ) {
        List<Point2> projectedPoints = new ArrayList<>();

        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return projectedPoints;
        }

        Frame3 frame = cylinder.frame();
        Point3 origin = frame.origin();
        Vec3 xAxis = frame.xAxis().toVec3();
        Vec3 yAxis = frame.yAxis().toVec3();
        Vec3 zAxis = frame.zAxis().toVec3();

        Double previousAngle = null;
        double radius = cylinder.radius();

        for (Point3 point : boundaryPoints) {
            Vec3 delta = point.subtract(origin);

            double radialX = delta.dot(xAxis);
            double radialY = delta.dot(yAxis);
            double axial = delta.dot(zAxis);

            double angle = Math.atan2(radialY, radialX);

            if (previousAngle != null) {
                while (angle - previousAngle > Math.PI) {
                    angle -= Math.PI * 2.0;
                }
                while (angle - previousAngle < -Math.PI) {
                    angle += Math.PI * 2.0;
                }
            }

            previousAngle = angle;
            projectedPoints.add(new Point2(radius * angle, axial));
        }

        for (Point2 projected : projectedPoints) {
            if (projected == null) {
                /*
                throw new IllegalArgumentException(
                        faceBoundLabel(face, boundIndex)
                                + ": cylindrical projected boundary points must not contain null points."
                );
                 */
                String label = (face == null || boundIndex < 0)
                        ? "Cylindrical boundary"
                        : faceBoundLabel(face, boundIndex);

                throw new IllegalArgumentException(
                        label + ": cylindrical projected boundary points must not contain null points."
                );
            }
        }

        return projectedPoints;
    }

    List<Point2> alignLoopToReferenceBand(
            List<Point2> loopPoints,
            List<Point2> referencePoints,
            double radius
    ) {
        if (loopPoints == null || loopPoints.isEmpty()) {
            return List.of();
        }
        if (referencePoints == null || referencePoints.isEmpty()) {
            return new ArrayList<>(loopPoints);
        }

        double period = 2.0 * Math.PI * radius;
        if (period == 0.0) {
            return new ArrayList<>(loopPoints);
        }

        double referenceMean = averageX(referencePoints);
        double loopMean = averageX(loopPoints);
        long shiftCount = Math.round((referenceMean - loopMean) / period);
        double shift = shiftCount * period;

        if (shift == 0.0) {
            return new ArrayList<>(loopPoints);
        }

        List<Point2> shifted = new ArrayList<>(loopPoints.size());
        for (Point2 point : loopPoints) {
            shifted.add(new Point2(point.x() + shift, point.y()));
        }

        return shifted;
    }

    private double averageX(List<Point2> points) {
        double sum = 0.0;
        for (Point2 point : points) {
            sum += point.x();
        }
        return sum / points.size();
    }

    private void validateBoundaryHasAtLeastThreePoints(
            List<Point3> boundaryPoints,
            FaceGeom face,
            int boundIndex,
            String stage
    ) {
        int count = boundaryPoints == null ? 0 : boundaryPoints.size();

        if (count < 3) {
            throw new IllegalArgumentException(
                    faceBoundLabel(face, boundIndex)
                            + ": boundary has only " + count
                            + " point(s) at stage '" + stage
                            + "'; at least 3 required for triangulation."
            );
        }
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

    private List<Point2> normalizeLoopToCompactBand(List<Point2> loopPoints, double radius) {
        if (loopPoints == null || loopPoints.isEmpty()) {
            return List.of();
        }

        double period = 2.0 * Math.PI * radius;
        if (period <= 0.0 || loopPoints.size() < 2) {
            return new ArrayList<>(loopPoints);
        }

        List<Double> sortedX = new ArrayList<>(loopPoints.size());
        for (Point2 point : loopPoints) {
            sortedX.add(point.x());
        }
        sortedX.sort(Double::compare);

        if (sortedX.size() < 2) {
            return new ArrayList<>(loopPoints);
        }

        double largestGap = Double.NEGATIVE_INFINITY;
        double gapStart = sortedX.getFirst();

        for (int i = 0; i < sortedX.size() - 1; i++) {
            double a = sortedX.get(i);
            double b = sortedX.get(i + 1);
            double gap = b - a;

            if (gap > largestGap) {
                largestGap = gap;
                gapStart = a;
            }
        }

        double wrapGap = (sortedX.getFirst() + period) - sortedX.getLast();
        if (wrapGap > largestGap) {
            return new ArrayList<>(loopPoints);
        }

        List<Point2> normalized = new ArrayList<>(loopPoints.size());
        for (Point2 point : loopPoints) {
            double x = point.x();
            if (x <= gapStart) {
                x += period;
            }
            normalized.add(new Point2(x, point.y()));
        }

        return normalized;
    }

    private String summarizeProjectedLoop(List<Point3> boundaryPoints, CylindricalSurface3 cylinder) {
        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return "no boundary points";
        }

        List<Point2> projected = projectBoundaryPointsTo2D(cylinder, boundaryPoints, null, -1);
        projected = normalizeLoopToCompactBand(projected, cylinder.radius());

        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        for (Point2 p : projected) {
            minX = Math.min(minX, p.x());
            maxX = Math.max(maxX, p.x());
            minY = Math.min(minY, p.y());
            maxY = Math.max(maxY, p.y());
        }

        StringBuilder sample = new StringBuilder();
        int limit = Math.min(projected.size(), 6);
        for (int i = 0; i < limit; i++) {
            Point2 p = projected.get(i);
            if (i > 0) {
                sample.append(" ; ");
            }
            sample.append("(")
                    .append(format(p.x()))
                    .append(", ")
                    .append(format(p.y()))
                    .append(")");
        }

        return "points=" + projected.size()
                + ", xRange=[" + format(minX) + ", " + format(maxX) + "]"
                + ", yRange=[" + format(minY) + ", " + format(maxY) + "]"
                + ", width=" + format(maxX - minX)
                + ", sample=" + sample;
    }


    private double minX(List<Point2> points) {
        double min = Double.POSITIVE_INFINITY;
        for (Point2 p : points) {
            min = Math.min(min, p.x());
        }
        return min;
    }

    private double maxX(List<Point2> points) {
        double max = Double.NEGATIVE_INFINITY;
        for (Point2 p : points) {
            max = Math.max(max, p.x());
        }
        return max;
    }

    private double minY(List<Point2> points) {
        double min = Double.POSITIVE_INFINITY;
        for (Point2 p : points) {
            min = Math.min(min, p.y());
        }
        return min;
    }

    private double maxY(List<Point2> points) {
        double max = Double.NEGATIVE_INFINITY;
        for (Point2 p : points) {
            max = Math.max(max, p.y());
        }
        return max;
    }

    private String format(double value) {
        return String.format(java.util.Locale.US, "%.6f", value);
    }


    private void logBoundaryCollapseDiagnostics(
            FaceGeom face,
            int boundIndex,
            List<List<Point3>> discretizedEdgePointLists,
            List<Point3> boundaryPoints,
            List<Point3> cleanedBoundaryPoints,
            List<Point3> openBoundaryPoints
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(faceBoundLabel(face, boundIndex))
                .append(": cylindrical boundary collapse diagnostics")
                .append(" | edgeLists=").append(discretizedEdgePointLists == null ? 0 : discretizedEdgePointLists.size())
                .append(" | raw=").append(boundaryPoints == null ? 0 : boundaryPoints.size())
                .append(" | cleaned=").append(cleanedBoundaryPoints == null ? 0 : cleanedBoundaryPoints.size())
                .append(" | open=").append(openBoundaryPoints == null ? 0 : openBoundaryPoints.size());

        if (discretizedEdgePointLists != null && !discretizedEdgePointLists.isEmpty()) {
            sb.append(" | perEdge=");
            for (int i = 0; i < discretizedEdgePointLists.size(); i++) {
                List<Point3> edgePoints = discretizedEdgePointLists.get(i);
                if (i > 0) {
                    sb.append(",");
                }
                sb.append(edgePoints == null ? 0 : edgePoints.size());
            }
        }

        AppConsole.log(sb.toString());
    }

    private void logBoundaryEdgeDetails(
            LoopGeom loop,
            FaceGeom face,
            int boundIndex
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append(faceBoundLabel(face, boundIndex))
                .append(": cylindrical boundary edge details");

        if (loop == null || loop.edges() == null || loop.edges().isEmpty()) {
            sb.append(" | no oriented edges");
            System.out.println(sb);
            return;
        }

        sb.append(" | edgeCount=").append(loop.edges().size());

        for (int i = 0; i < loop.edges().size(); i++) {
            OrientedEdgeGeom orientedEdge = loop.edges().get(i);
            EdgeGeom edge = orientedEdge == null ? null : orientedEdge.edge();
            Object curve = edge == null ? null : edge.curve();

            sb.append(" | [").append(i).append("]")
                    .append(" oe=").append(orientedEdge == null ? "null" : orientedEdge.stepId())
                    .append(", e=").append(edge == null ? "null" : edge.stepId())
                    .append(", curve=").append(curve == null ? "null" : curve.getClass().getSimpleName())
                    .append(", orient=").append(orientedEdge != null && orientedEdge.orientation());
        }

        AppConsole.log(sb.toString());
    }

    private boolean isTwoLineStripLoop(LoopGeom loop) {
        if (loop == null || loop.edges() == null || loop.edges().size() != 2) {
            return false;
        }

        for (OrientedEdgeGeom orientedEdge : loop.edges()) {
            if (orientedEdge == null || orientedEdge.edge() == null || orientedEdge.edge().curve() == null) {
                return false;
            }
            if (!(orientedEdge.edge().curve() instanceof LineCurve3)) {
                return false;
            }
        }

        return true;
    }


    private double distance(Point3 a, Point3 b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        double dz = a.z() - b.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    private FaceMeshPatch tessellateTwoLineStrip(FaceGeom face, LoopGeom loop) {
        List<List<Point3>> discretizedEdgePointLists =
                shared.collectDiscretizedEdgePoints(loop, face, 0);

        if (discretizedEdgePointLists == null || discretizedEdgePointLists.size() != 2) {
            throw new IllegalArgumentException(
                    faceLabel(face) + ": two-line cylindrical strip must have exactly 2 discretized edge point lists."
            );
        }

        List<Point3> edgeA = new ArrayList<>(discretizedEdgePointLists.get(0));
        List<Point3> edgeB = new ArrayList<>(discretizedEdgePointLists.get(1));

        if (edgeA.size() < 2 || edgeB.size() < 2) {
            throw new IllegalArgumentException(
                    faceLabel(face) + ": two-line cylindrical strip edges must each have at least 2 points."
            );
        }

        double sameDirectionCost =
                distance(edgeA.getFirst(), edgeB.getFirst()) + distance(edgeA.getLast(), edgeB.getLast());
        double reversedDirectionCost =
                distance(edgeA.getFirst(), edgeB.getLast()) + distance(edgeA.getLast(), edgeB.getFirst());

        if (reversedDirectionCost < sameDirectionCost) {
            java.util.Collections.reverse(edgeB);
        }

        int segmentCount = Math.min(edgeA.size(), edgeB.size());
        if (segmentCount < 2) {
            throw new IllegalArgumentException(
                    faceLabel(face) + ": two-line cylindrical strip must have at least 2 aligned points per edge."
            );
        }

        List<Point3> boundaryPoints = new ArrayList<>(segmentCount * 2);
        for (int i = 0; i < segmentCount; i++) {
            boundaryPoints.add(edgeA.get(i));
            boundaryPoints.add(edgeB.get(i));
        }

        List<int[]> triangles = new ArrayList<>();
        for (int i = 0; i < segmentCount - 1; i++) {
            int a0 = i * 2;
            int b0 = i * 2 + 1;
            int a1 = (i + 1) * 2;
            int b1 = (i + 1) * 2 + 1;

            triangles.add(new int[]{a0, b0, a1});
            triangles.add(new int[]{a1, b0, b1});
        }

        if (triangles.isEmpty()) {
            throw new IllegalArgumentException(
                    faceLabel(face) + ": two-line cylindrical strip produced no triangles."
            );
        }

        return shared.buildFaceMeshPatch(boundaryPoints, triangles,SurfaceFamily.CYLINDRICAL);
    }

    private void logPreparedCylindricalLoops(
            FaceGeom face,
            List<PlanarFaceTessellator.PreparedLoop> preparedLoops
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("CYL_DEBUG ")
                .append(faceLabel(face))
                .append(": cylindrical prepared loop diagnostics");

        if (preparedLoops == null || preparedLoops.isEmpty()) {
            sb.append(" | no prepared loops");
            System.out.println(sb);
            return;
        }

        sb.append(" | loopCount=").append(preparedLoops.size());

        for (int i = 0; i < preparedLoops.size(); i++) {
            PlanarFaceTessellator.PreparedLoop preparedLoop = preparedLoops.get(i);
            List<Point2> points = preparedLoop == null || preparedLoop.polygonLoop() == null
                    ? List.of()
                    : preparedLoop.polygonLoop().points();

            int count = points.size();
            double minX = count == 0 ? Double.NaN : minX(points);
            double maxX = count == 0 ? Double.NaN : maxX(points);
            double minY = count == 0 ? Double.NaN : minY(points);
            double maxY = count == 0 ? Double.NaN : maxY(points);
            double width = count == 0 ? Double.NaN : (maxX - minX);
            double height = count == 0 ? Double.NaN : (maxY - minY);

            sb.append(" | [").append(i).append("]")
                    .append(" count=").append(count)
                    .append(", xRange=[").append(format(minX)).append(", ").append(format(maxX)).append("]")
                    .append(", yRange=[").append(format(minY)).append(", ").append(format(maxY)).append("]")
                    .append(", width=").append(format(width))
                    .append(", height=").append(format(height))
                    .append(", area=").append(format(PolygonMath.signedArea(points)))
                    .append(", sample=").append(samplePoints(points, 6));
        }

        System.out.println(sb);
    }

    private String samplePoints(List<Point2> points, int limit) {
        if (points == null || points.isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        int actualLimit = Math.min(points.size(), limit);
        for (int i = 0; i < actualLimit; i++) {
            Point2 p = points.get(i);
            if (i > 0) {
                sb.append(" ; ");
            }
            sb.append("(")
                    .append(format(p.x()))
                    .append(", ")
                    .append(format(p.y()))
                    .append(")");
        }

        if (points.size() > actualLimit) {
            sb.append(" ; ...");
        }

        sb.append("]");
        return sb.toString();
    }

    private List<PlanarFaceTessellator.PreparedLoop> reorderPreparedLoopsOuterFirst(
            List<PlanarFaceTessellator.PreparedLoop> preparedLoops
    ) {
        if (preparedLoops == null || preparedLoops.size() <= 1) {
            return preparedLoops;
        }

        List<PlanarFaceTessellator.PreparedLoop> reordered = new ArrayList<>(preparedLoops);

        int outerIndex = 0;
        double maxAbsArea = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < reordered.size(); i++) {
            List<Point2> points = reordered.get(i).polygonLoop().points();
            double absArea = Math.abs(PolygonMath.signedArea(points));
            if (absArea > maxAbsArea) {
                maxAbsArea = absArea;
                outerIndex = i;
            }
        }

        if (outerIndex == 0) {
            return reordered;
        }

        PlanarFaceTessellator.PreparedLoop outer = reordered.remove(outerIndex);
        reordered.addFirst(outer);
        return reordered;
    }


    private PlanarFaceTessellator.SimplifiedProjectedLoop removeProjectedCollinearPoints(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        if (boundaryPoints.size() != projectedPoints.size()) {
            throw new IllegalArgumentException("Boundary/projected point counts must match.");
        }

        if (projectedPoints.size() < 4) {
            return new PlanarFaceTessellator.SimplifiedProjectedLoop(boundaryPoints, projectedPoints);
        }

        List<Point3> filteredBoundary = new ArrayList<>();
        List<Point2> filteredProjected = new ArrayList<>();

        int n = projectedPoints.size();

        for (int i = 0; i < n; i++) {
            Point2 prev = projectedPoints.get((i - 1 + n) % n);
            Point2 curr = projectedPoints.get(i);
            Point2 next = projectedPoints.get((i + 1) % n);

            double area2 =
                    (curr.x() - prev.x()) * (next.y() - prev.y()) -
                            (curr.y() - prev.y()) * (next.x() - prev.x());

            double dx1 = curr.x() - prev.x();
            double dy1 = curr.y() - prev.y();
            double dx2 = next.x() - curr.x();
            double dy2 = next.y() - curr.y();

            double len1 = Math.hypot(dx1, dy1);
            double len2 = Math.hypot(dx2, dy2);
            double scale = Math.max(len1 * len2, 1.0);
            double collinearEpsilon = 1e-9 * scale;

            boolean nearlyCollinear = Math.abs(area2) <= collinearEpsilon;

            if (!nearlyCollinear) {
                filteredBoundary.add(boundaryPoints.get(i));
                filteredProjected.add(curr);
            }
        }

        if (filteredProjected.size() < 3) {
            return new PlanarFaceTessellator.SimplifiedProjectedLoop(boundaryPoints, projectedPoints);
        }

        return new PlanarFaceTessellator.SimplifiedProjectedLoop(filteredBoundary, filteredProjected);
    }


    private String orthogonalSegmentStats(List<Point2> points) {
        if (points == null || points.size() < 2) {
            return "segments=0";
        }

        int horizontal = 0;
        int vertical = 0;
        int other = 0;

        for (int i = 0; i < points.size(); i++) {
            Point2 a = points.get(i);
            Point2 b = points.get((i + 1) % points.size());

            double dx = Math.abs(b.x() - a.x());
            double dy = Math.abs(b.y() - a.y());

            if (dx <= tolerance.pointEqualityEpsilon() * 10.0 && dy > tolerance.pointEqualityEpsilon() * 10.0) {
                vertical++;
            } else if (dy <= tolerance.pointEqualityEpsilon() * 10.0 && dx > tolerance.pointEqualityEpsilon() * 10.0) {
                horizontal++;
            } else {
                other++;
            }
        }

        return "segments[h=" + horizontal + ", v=" + vertical + ", other=" + other + "]";
    }

    private boolean isOrthogonalSingleLoop(
            List<PlanarFaceTessellator.PreparedLoop> preparedLoops
    ) {
        if (preparedLoops == null || preparedLoops.size() != 1) {
            return false;
        }

        List<Point2> points = preparedLoops.getFirst().polygonLoop().points();
        if (points.size() < 4) {
            return false;
        }

        int other = 0;
        double eps = tolerance.pointEqualityEpsilon() * 10.0;

        for (int i = 0; i < points.size(); i++) {
            Point2 a = points.get(i);
            Point2 b = points.get((i + 1) % points.size());

            double dx = Math.abs(b.x() - a.x());
            double dy = Math.abs(b.y() - a.y());

            boolean vertical = dx <= eps && dy > eps;
            boolean horizontal = dy <= eps && dx > eps;

            if (!vertical && !horizontal) {
                other++;
            }
        }

        return other == 0;
    }

    private List<int[]> tryOrthogonalSingleLoopFallbackTriangles(List<Point2> points) {
        List<int[]> triangles = new ArrayList<>();

        if (points == null || points.size() < 3) {
            return triangles;
        }

        int anchor = findGoodAnchor(points);
        for (int i = 1; i < points.size() - 1; i++) {
            int a = anchor;
            int b = i;
            int c = i + 1;

            if (!isDegenerate(points.get(a), points.get(b), points.get(c))) {
                triangles.add(new int[]{a, b, c});
            }
        }

        return triangles;
    }

    private int findGoodAnchor(List<Point2> points) {
        int best = 0;
        for (int i = 1; i < points.size(); i++) {
            Point2 p = points.get(i);
            Point2 b = points.get(best);

            if (p.y() < b.y() || (p.y() == b.y() && p.x() < b.x())) {
                best = i;
            }
        }
        return best;
    }

    private boolean isDegenerate(Point2 a, Point2 b, Point2 c) {
        double area2 =
                (b.x() - a.x()) * (c.y() - a.y()) -
                        (b.y() - a.y()) * (c.x() - a.x());

        return Math.abs(area2) <= tolerance.pointEqualityEpsilon() * 10.0;
    }

    private PlanarFaceTessellator.SimplifiedProjectedLoop collapseHorizontalPlateaus(
            FaceGeom face,
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {
        if (boundaryPoints.size() != projectedPoints.size()) {
            throw new IllegalArgumentException("Boundary/projected point counts must match.");
        }

        if (projectedPoints.size() < 4) {
            return new PlanarFaceTessellator.SimplifiedProjectedLoop(boundaryPoints, projectedPoints);
        }

        List<Point3> filteredBoundary = new ArrayList<>();
        List<Point2> filteredProjected = new ArrayList<>();

        int n = projectedPoints.size();
        double eps = tolerance.pointEqualityEpsilon() * 1000.0;

        for (int i = 0; i < n; i++) {
            Point2 prev = projectedPoints.get((i - 1 + n) % n);
            Point2 curr = projectedPoints.get(i);
            Point2 next = projectedPoints.get((i + 1) % n);

            double minY = Math.min(prev.y(), Math.min(curr.y(), next.y()));
            double maxY = Math.max(prev.y(), Math.max(curr.y(), next.y()));

            boolean nearHorizontalBand = (maxY - minY) <= eps;

            boolean xBetween =
                    curr.x() >= Math.min(prev.x(), next.x()) - eps &&
                            curr.x() <= Math.max(prev.x(), next.x()) + eps;

            boolean interiorHorizontalPlateauPoint = nearHorizontalBand && xBetween;




            boolean nearSameYAsPrev = Math.abs(curr.y() - prev.y()) <= eps;
            boolean nearSameYAsNext = Math.abs(curr.y() - next.y()) <= eps;
            boolean prevAndNextNearSameY = Math.abs(prev.y() - next.y()) <= eps;

            boolean currBetweenPrevNext =
                    curr.x() >= Math.min(prev.x(), next.x()) - eps &&
                            curr.x() <= Math.max(prev.x(), next.x()) + eps;

            boolean removableBridgePoint =
                    nearSameYAsPrev &&
                            nearSameYAsNext &&
                            prevAndNextNearSameY &&
                            currBetweenPrevNext;














            Point2 next2 = projectedPoints.get((i + 2) % n);

            double minYForward = Math.min(curr.y(), Math.min(next.y(), next2.y()));
            double maxYForward = Math.max(curr.y(), Math.max(next.y(), next2.y()));

            boolean forwardHorizontalBand = (maxYForward - minYForward) <= eps;

            boolean currBetweenForward =
                    curr.x() >= Math.min(next.x(), next2.x()) - eps &&
                            curr.x() <= Math.max(next.x(), next2.x()) + eps;


            boolean removableForwardPlateauPoint = forwardHorizontalBand && currBetweenForward;





            boolean removed = interiorHorizontalPlateauPoint || removableForwardPlateauPoint || removableBridgePoint;

            if (face != null && "#104057".equals(face.stepId())) {
                AppConsole.log(
                        "CYL_PLATEAU_SCAN Face #104057"
                                + " | i=" + i
                                + " | prev=" + prev
                                + " | curr=" + curr
                                + " | next=" + next
                                + " | remove=" + removed
                );
            }

            if (!interiorHorizontalPlateauPoint && !removableForwardPlateauPoint && !removableBridgePoint) {
                filteredBoundary.add(boundaryPoints.get(i));
                filteredProjected.add(curr);
            }
        }

        if (filteredProjected.size() < 3) {
            return new PlanarFaceTessellator.SimplifiedProjectedLoop(boundaryPoints, projectedPoints);
        }

        return new PlanarFaceTessellator.SimplifiedProjectedLoop(filteredBoundary, filteredProjected);
    }
}