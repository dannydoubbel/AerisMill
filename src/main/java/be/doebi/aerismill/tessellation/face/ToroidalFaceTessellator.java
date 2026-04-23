package be.doebi.aerismill.tessellation.face;


import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.ToroidalSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.Point2;
import be.doebi.aerismill.tessellation.polygon.PolygonLoop2;
import be.doebi.aerismill.tessellation.polygon.PolygonMath;
import be.doebi.aerismill.tessellation.polygon.PolygonTriangulator;
import be.doebi.aerismill.tessellation.polygon.PolygonWithHoles2;

import java.util.ArrayList;
import java.util.List;

public final class ToroidalFaceTessellator implements FaceTessellator {

    private final PlanarFaceTessellator shared;
    private final GeometryTolerance tolerance;

    public ToroidalFaceTessellator(
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
        if (!(face.surface() instanceof ToroidalSurface3 torus)) {
            throw new IllegalArgumentException(faceLabel(face) + ": only toroidal faces are supported here.");
        }
        if (face.bounds() == null || face.bounds().isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": face must have at least one bound.");
        }

        List<PlanarFaceTessellator.PreparedLoop> preparedLoops =
                prepareProjectedPolygonLoops(face, torus);

        preparedLoops = reorderPreparedLoopsOuterFirst(preparedLoops);

        PolygonWithHoles2 polygon = shared.buildPolygonWithHoles(preparedLoops);
        shared.validateHoleRelationships(polygon);

        PlanarFaceTessellator.TriangulationResult triangulation =
                shared.triangulatePolygon(polygon);

        List<Point2> projectedBoundaryPoints = triangulation.points();
        List<int[]> triangles = triangulation.triangles();

        List<Point2> originalProjectedBoundaryPoints =
                shared.collectPreparedProjectedPoints(preparedLoops);
        List<Point3> originalBoundaryPoints =
                shared.collectPreparedBoundaryPoints(preparedLoops);

        List<Point3> boundaryPoints = alignBoundaryPointsToTriangulation(
                originalProjectedBoundaryPoints,
                originalBoundaryPoints,
                projectedBoundaryPoints
        );

        shared.validateTrianglesNotEmpty(triangles, face, preparedLoops, boundaryPoints);
        shared.validateTriangleIndices(projectedBoundaryPoints, triangles);
        shared.validateTrianglesAreNonDegenerate(triangles);
        shared.validateTrianglesHavePositiveArea(projectedBoundaryPoints, triangles);

        return shared.buildFaceMeshPatch(boundaryPoints, triangles, SurfaceFamily.TOROIDAL);
    }

    List<PlanarFaceTessellator.PreparedLoop> prepareProjectedPolygonLoops(
            FaceGeom face,
            ToroidalSurface3 torus
    ) {
        List<LoopGeom> bounds = face.bounds();

        if (bounds == null || bounds.isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": face must have at least one bound.");
        }

        List<PlanarFaceTessellator.PreparedLoop> preparedLoops = new ArrayList<>();

        for (int i = 0; i < bounds.size(); i++) {
            PlanarFaceTessellator.PreparedLoop prepared =
                    prepareProjectedPolygonLoop(face, bounds.get(i), torus, i);

            if (i > 0) {
                List<Point2> aligned =
                        alignLoopToReferenceBand(
                                prepared.polygonLoop().points(),
                                preparedLoops.getFirst().polygonLoop().points(),
                                torus
                        );

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
            ToroidalSurface3 torus,
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

        validateBoundaryHasAtLeastThreePoints(openBoundaryPoints, face, boundIndex, "after-3d-cleanup");

        List<Point2> projectedBoundaryPoints =
                projectBoundaryPointsTo2D(torus, openBoundaryPoints, face, boundIndex);

        projectedBoundaryPoints = normalizeLoopToCompactBand(
                projectedBoundaryPoints,
                2.0 * Math.PI * torus.majorRadius(),
                2.0 * Math.PI * torus.minorRadius()
        );

        PlanarFaceTessellator.SimplifiedProjectedLoop simplifiedLoop =
                shared.simplifyProjectedLoop(openBoundaryPoints, projectedBoundaryPoints);

        validateBoundaryHasAtLeastThreePoints(
                simplifiedLoop.boundaryPoints(),
                face,
                boundIndex,
                "after-toroidal-projection"
        );

        PolygonLoop2 polygonLoop = shared.buildOuterPolygonLoop(simplifiedLoop.projectedPoints());
        shared.validateProjectedBoundaryIsSimple(polygonLoop, face, boundIndex);

        return new PlanarFaceTessellator.PreparedLoop(
                simplifiedLoop.boundaryPoints(),
                polygonLoop
        );
    }

    List<Point2> projectBoundaryPointsTo2D(
            ToroidalSurface3 torus,
            List<Point3> boundaryPoints,
            FaceGeom face,
            int boundIndex
    ) {
        List<Point2> projectedPoints = new ArrayList<>();

        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return projectedPoints;
        }

        Frame3 frame = torus.frame();
        Point3 origin = frame.origin();
        Vec3 xAxis = frame.xAxis().toVec3();
        Vec3 yAxis = frame.yAxis().toVec3();
        Vec3 zAxis = frame.zAxis().toVec3();

        Double previousU = null;
        Double previousV = null;

        for (Point3 point : boundaryPoints) {
            Vec3 delta = point.subtract(origin);

            double x = delta.dot(xAxis);
            double y = delta.dot(yAxis);
            double z = delta.dot(zAxis);

            double u = Math.atan2(y, x);

            double radialDistance = Math.hypot(x, y);
            double tubeX = radialDistance - torus.majorRadius();
            double v = Math.atan2(z, tubeX);

            if (previousU != null) {
                while (u - previousU > Math.PI) {
                    u -= Math.PI * 2.0;
                }
                while (u - previousU < -Math.PI) {
                    u += Math.PI * 2.0;
                }
            }

            if (previousV != null) {
                while (v - previousV > Math.PI) {
                    v -= Math.PI * 2.0;
                }
                while (v - previousV < -Math.PI) {
                    v += Math.PI * 2.0;
                }
            }

            previousU = u;
            previousV = v;

            projectedPoints.add(new Point2(
                    torus.majorRadius() * u,
                    torus.minorRadius() * v
            ));
        }

        for (Point2 projected : projectedPoints) {
            if (projected == null) {
                throw new IllegalArgumentException(
                        faceBoundLabel(face, boundIndex)
                                + ": toroidal projected boundary points must not contain null points."
                );
            }
        }

        return projectedPoints;
    }

    List<Point2> alignLoopToReferenceBand(
            List<Point2> loopPoints,
            List<Point2> referencePoints,
            ToroidalSurface3 torus
    ) {
        if (loopPoints == null || loopPoints.isEmpty()) {
            return List.of();
        }
        if (referencePoints == null || referencePoints.isEmpty()) {
            return new ArrayList<>(loopPoints);
        }

        double uPeriod = 2.0 * Math.PI * torus.majorRadius();
        double vPeriod = 2.0 * Math.PI * torus.minorRadius();

        double referenceMeanX = averageX(referencePoints);
        double loopMeanX = averageX(loopPoints);
        long uShiftCount = uPeriod == 0.0 ? 0 : Math.round((referenceMeanX - loopMeanX) / uPeriod);
        double xShift = uShiftCount * uPeriod;

        double referenceMeanY = averageY(referencePoints);
        double loopMeanY = averageY(loopPoints);
        long vShiftCount = vPeriod == 0.0 ? 0 : Math.round((referenceMeanY - loopMeanY) / vPeriod);
        double yShift = vShiftCount * vPeriod;

        if (xShift == 0.0 && yShift == 0.0) {
            return new ArrayList<>(loopPoints);
        }

        List<Point2> shifted = new ArrayList<>(loopPoints.size());
        for (Point2 point : loopPoints) {
            shifted.add(new Point2(point.x() + xShift, point.y() + yShift));
        }

        return shifted;
    }

    private List<Point2> normalizeLoopToCompactBand(
            List<Point2> loopPoints,
            double xPeriod,
            double yPeriod
    ) {
        List<Point2> normalized = new ArrayList<>(loopPoints);
        normalized = normalizeLoopAxisToCompactBand(normalized, xPeriod, true);
        normalized = normalizeLoopAxisToCompactBand(normalized, yPeriod, false);
        return normalized;
    }

    private List<Point2> normalizeLoopAxisToCompactBand(
            List<Point2> loopPoints,
            double period,
            boolean xAxis
    ) {
        if (loopPoints == null || loopPoints.isEmpty()) {
            return List.of();
        }
        if (period <= 0.0 || loopPoints.size() < 2) {
            return new ArrayList<>(loopPoints);
        }

        List<Double> sorted = new ArrayList<>(loopPoints.size());
        for (Point2 point : loopPoints) {
            sorted.add(xAxis ? point.x() : point.y());
        }
        sorted.sort(Double::compare);

        if (sorted.size() < 2) {
            return new ArrayList<>(loopPoints);
        }

        double largestGap = Double.NEGATIVE_INFINITY;
        double gapStart = sorted.getFirst();

        for (int i = 0; i < sorted.size() - 1; i++) {
            double a = sorted.get(i);
            double b = sorted.get(i + 1);
            double gap = b - a;

            if (gap > largestGap) {
                largestGap = gap;
                gapStart = a;
            }
        }

        double wrapGap = (sorted.getFirst() + period) - sorted.getLast();
        if (wrapGap > largestGap) {
            return new ArrayList<>(loopPoints);
        }

        List<Point2> adjusted = new ArrayList<>(loopPoints.size());
        for (Point2 point : loopPoints) {
            double x = point.x();
            double y = point.y();

            if (xAxis) {
                if (x <= gapStart) {
                    x += period;
                }
            } else {
                if (y <= gapStart) {
                    y += period;
                }
            }

            adjusted.add(new Point2(x, y));
        }

        return adjusted;
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

        PlanarFaceTessellator.PreparedLoop outer = reordered.remove(outerIndex);
        reordered.addFirst(outer);
        return reordered;
    }

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

    private double averageX(List<Point2> points) {
        double sum = 0.0;
        for (Point2 point : points) {
            sum += point.x();
        }
        return sum / points.size();
    }

    private double averageY(List<Point2> points) {
        double sum = 0.0;
        for (Point2 point : points) {
            sum += point.y();
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
}