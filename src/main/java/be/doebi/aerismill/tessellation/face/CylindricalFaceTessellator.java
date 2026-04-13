package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.CylindricalSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.Point2;
import be.doebi.aerismill.tessellation.polygon.PolygonLoop2;
import be.doebi.aerismill.tessellation.polygon.PolygonTriangulator;
import be.doebi.aerismill.tessellation.polygon.PolygonWithHoles2;

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

        List<PlanarFaceTessellator.PreparedLoop> preparedLoops =
                prepareProjectedPolygonLoops(face, cylinder);

        if (isNarrowStripCase(preparedLoops)) {
            List<Point3> boundaryPoints = shared.collectPreparedBoundaryPoints(preparedLoops);
            List<int[]> triangles = buildTriangleFan(boundaryPoints, face);

            return shared.buildFaceMeshPatch(boundaryPoints, triangles);
        }




        PolygonWithHoles2 polygon = shared.buildPolygonWithHoles(preparedLoops);
        shared.validateHoleRelationships(polygon);

        List<int[]> triangles = shared.triangulatePolygon(polygon);

        List<Point3> boundaryPoints = shared.collectPreparedBoundaryPoints(preparedLoops);
        List<Point2> projectedBoundaryPoints = shared.collectPreparedProjectedPoints(preparedLoops);

        shared.validateTrianglesNotEmpty(triangles, face, preparedLoops, boundaryPoints);
        shared.validateTriangleIndices(boundaryPoints, triangles);
        shared.validateTrianglesAreNonDegenerate(triangles);
        shared.validateTrianglesHavePositiveArea(projectedBoundaryPoints, triangles);

        return shared.buildFaceMeshPatch(boundaryPoints, triangles);
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

        validateBoundaryHasAtLeastThreePoints(openBoundaryPoints, face, boundIndex, "after-3d-cleanup");

        try {
            /*
            List<Point2> projectedBoundaryPoints =
                    projectBoundaryPointsTo2D(cylinder, openBoundaryPoints, face, boundIndex);

            projectedBoundaryPoints =
                    normalizeLoopToCompactBand(projectedBoundaryPoints, cylinder.radius());

            PlanarFaceTessellator.SimplifiedProjectedLoop simplifiedLoop =
                    shared.simplifyProjectedLoop(openBoundaryPoints, projectedBoundaryPoints);


            */
            List<Point2> projectedBoundaryPoints =
                    projectBoundaryPointsTo2D(cylinder, openBoundaryPoints, face, boundIndex);

            projectedBoundaryPoints =
                    normalizeLoopToCompactBand(projectedBoundaryPoints, cylinder.radius());

            double width = maxX(projectedBoundaryPoints) - minX(projectedBoundaryPoints);
            double height = maxY(projectedBoundaryPoints) - minY(projectedBoundaryPoints);

            if (width <= tolerance.pointEqualityEpsilon() * 10.0 || (height > 0.0 && width < height * 1.0e-4)) {
                return new PlanarFaceTessellator.PreparedLoop(
                        openBoundaryPoints,
                        new PolygonLoop2(projectedBoundaryPoints)
                );
            }

            PlanarFaceTessellator.SimplifiedProjectedLoop simplifiedLoop =
                    shared.simplifyProjectedLoop(openBoundaryPoints, projectedBoundaryPoints);



            validateBoundaryHasAtLeastThreePoints(
                    simplifiedLoop.boundaryPoints(),
                    face,
                    boundIndex,
                    "after-cylindrical-projection"
            );

            PolygonLoop2 polygonLoop = shared.buildOuterPolygonLoop(simplifiedLoop.projectedPoints());
            shared.validateProjectedBoundaryIsSimple(polygonLoop, face, boundIndex);

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

    private boolean isNarrowStripCase(List<PlanarFaceTessellator.PreparedLoop> preparedLoops) {
        if (preparedLoops == null || preparedLoops.size() != 1) {
            return false;
        }

        List<Point2> points = preparedLoops.getFirst().polygonLoop().points();
        if (points == null || points.size() < 3) {
            return false;
        }

        double width = maxX(points) - minX(points);
        double height = maxY(points) - minY(points);

        return width <= tolerance.pointEqualityEpsilon() * 10.0
                || (height > 0.0 && width < height * 1.0e-4);
    }

    private List<int[]> buildTriangleFan(List<Point3> boundaryPoints, FaceGeom face) {
        if (boundaryPoints == null || boundaryPoints.size() < 3) {
            throw new IllegalArgumentException(faceLabel(face) + ": not enough points for cylindrical strip fallback.");
        }

        List<int[]> triangles = new ArrayList<>();
        for (int i = 1; i < boundaryPoints.size() - 1; i++) {
            triangles.add(new int[]{0, i, i + 1});
        }

        return triangles;
    }
}