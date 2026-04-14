package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.ConicalSurface3;
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

public final class ConicalFaceTessellator implements FaceTessellator {

    private final PlanarFaceTessellator shared;
    private final GeometryTolerance tolerance;

    public ConicalFaceTessellator(
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
        if (!(face.surface() instanceof ConicalSurface3 cone)) {
            throw new IllegalArgumentException(faceLabel(face) + ": only conical faces are supported here.");
        }
        if (face.bounds() == null || face.bounds().isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": face must have at least one bound.");
        }

        List<PlanarFaceTessellator.PreparedLoop> preparedLoops =
                prepareProjectedPolygonLoops(face, cone);

        PolygonWithHoles2 polygon = shared.buildPolygonWithHoles(preparedLoops);
        shared.validateHoleRelationships(polygon);

        List<int[]> triangles = shared.triangulatePolygon(polygon);

        List<Point3> boundaryPoints = shared.collectPreparedBoundaryPoints(preparedLoops);
        List<Point2> projectedBoundaryPoints = shared.collectPreparedProjectedPoints(preparedLoops);

        shared.validateTrianglesNotEmpty(triangles, face, preparedLoops, boundaryPoints);
        shared.validateTriangleIndices(boundaryPoints, triangles);
        shared.validateTrianglesAreNonDegenerate(triangles);
        shared.validateTrianglesHavePositiveArea(projectedBoundaryPoints, triangles);

        return shared.buildFaceMeshPatch(boundaryPoints, triangles,SurfaceFamily.CONICAL);
    }

    List<PlanarFaceTessellator.PreparedLoop> prepareProjectedPolygonLoops(
            FaceGeom face,
            ConicalSurface3 cone
    ) {
        List<LoopGeom> bounds = face.bounds();

        if (bounds == null || bounds.isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": face must have at least one bound.");
        }

        List<PlanarFaceTessellator.PreparedLoop> preparedLoops = new ArrayList<>();

        for (int i = 0; i < bounds.size(); i++) {
            PlanarFaceTessellator.PreparedLoop prepared =
                    prepareProjectedPolygonLoop(face, bounds.get(i), cone, i);

            if (i > 0) {
                List<Point2> aligned =
                        alignLoopToReferenceBand(
                                prepared.polygonLoop().points(),
                                preparedLoops.getFirst().polygonLoop().points(),
                                cone
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
            ConicalSurface3 cone,
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
                projectBoundaryPointsTo2D(cone, openBoundaryPoints, face, boundIndex);

        PlanarFaceTessellator.SimplifiedProjectedLoop simplifiedLoop =
                shared.simplifyProjectedLoop(openBoundaryPoints, projectedBoundaryPoints);

        validateBoundaryHasAtLeastThreePoints(
                simplifiedLoop.boundaryPoints(),
                face,
                boundIndex,
                "after-conical-projection"
        );

        PolygonLoop2 polygonLoop = shared.buildOuterPolygonLoop(simplifiedLoop.projectedPoints());
        shared.validateProjectedBoundaryIsSimple(polygonLoop, face, boundIndex);

        return new PlanarFaceTessellator.PreparedLoop(
                simplifiedLoop.boundaryPoints(),
                polygonLoop
        );
    }

    List<Point2> projectBoundaryPointsTo2D(
            ConicalSurface3 cone,
            List<Point3> boundaryPoints,
            FaceGeom face,
            int boundIndex
    ) {
        List<Point2> projectedPoints = new ArrayList<>();

        if (boundaryPoints == null || boundaryPoints.isEmpty()) {
            return projectedPoints;
        }

        Frame3 frame = cone.frame();
        Point3 origin = frame.origin();
        Vec3 xAxis = frame.xAxis().toVec3();
        Vec3 yAxis = frame.yAxis().toVec3();
        Vec3 zAxis = frame.zAxis().toVec3();

        double tanSemiAngle = Math.tan(cone.semiAngle());
        Double previousAngle = null;

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

            double localRadius = cone.radius() + axial * tanSemiAngle;
            double unwrapScale = Math.abs(localRadius);

            projectedPoints.add(new Point2(unwrapScale * angle, axial));
        }

        for (Point2 projected : projectedPoints) {
            if (projected == null) {
                throw new IllegalArgumentException(
                        faceBoundLabel(face, boundIndex)
                                + ": conical projected boundary points must not contain null points."
                );
            }
        }

        return projectedPoints;
    }

    List<Point2> alignLoopToReferenceBand(
            List<Point2> loopPoints,
            List<Point2> referencePoints,
            ConicalSurface3 cone
    ) {
        if (loopPoints == null || loopPoints.isEmpty()) {
            return List.of();
        }
        if (referencePoints == null || referencePoints.isEmpty()) {
            return new ArrayList<>(loopPoints);
        }

        double representativeRadius = representativeRadius(loopPoints, cone);
        double period = 2.0 * Math.PI * representativeRadius;
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

    private double representativeRadius(List<Point2> loopPoints, ConicalSurface3 cone) {
        double tanSemiAngle = Math.tan(cone.semiAngle());
        double sum = 0.0;

        for (Point2 point : loopPoints) {
            double axial = point.y();
            double localRadius = Math.abs(cone.radius() + axial * tanSemiAngle);
            sum += localRadius;
        }

        double average = sum / loopPoints.size();
        return average == 0.0 ? Math.abs(cone.radius()) : average;
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
}