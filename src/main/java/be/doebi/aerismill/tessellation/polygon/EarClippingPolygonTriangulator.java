package be.doebi.aerismill.tessellation.polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EarClippingPolygonTriangulator implements PolygonTriangulator {

    @Override
    public List<int[]> triangulate(PolygonWithHoles2 polygon) {
        if (polygon == null) {
            throw new IllegalArgumentException("Polygon must not be null.");
        }
        if (polygon.outer() == null) {
            throw new IllegalArgumentException("Polygon outer loop must not be null.");
        }
        if (polygon.holes() == null) {
            throw new IllegalArgumentException("Polygon holes must not be null.");
        }

        List<Point2> outerPoints = polygon.outer().points();
        if (outerPoints == null) {
            throw new IllegalArgumentException("Polygon outer loop points must not be null.");
        }
        if (outerPoints.size() < 3) {
            throw new IllegalArgumentException("Polygon outer loop must contain at least three points.");
        }

        if (polygon.holes().isEmpty()) {
            return triangulateSimplePolygon(outerPoints, buildNormalizedOuterIndices(outerPoints));
        }

        if (polygon.holes().size() > 1) {
            throw new UnsupportedOperationException("Multiple polygon holes are not supported yet.");
        }

        PolygonLoop2 hole = polygon.holes().getFirst();
        if (hole == null) {
            throw new IllegalArgumentException("Polygon hole loop must not be null.");
        }
        if (hole.points() == null) {
            throw new IllegalArgumentException("Polygon hole loop points must not be null.");
        }
        if (hole.points().size() < 3) {
            throw new IllegalArgumentException("Polygon hole loop must contain at least three points.");
        }

        List<Point2> allPoints = new ArrayList<>();
        allPoints.addAll(outerPoints);
        allPoints.addAll(hole.points());

        List<Integer> outerIndices = buildNormalizedOuterIndices(outerPoints);
        List<Integer> holeIndices = buildNormalizedHoleIndices(hole.points(), outerPoints.size());

        List<Integer> bridgedPolygon = bridgeSingleHoleIntoOuterLoop(
                allPoints,
                outerPoints,
                hole.points(),
                outerIndices,
                holeIndices
        );

        return triangulateSimplePolygon(allPoints, bridgedPolygon);
    }

    List<int[]> triangulateSimplePolygon(List<Point2> points, List<Integer> polygonIndices) {
        if (points == null) {
            throw new IllegalArgumentException("Points must not be null.");
        }
        if (polygonIndices == null) {
            throw new IllegalArgumentException("Polygon indices must not be null.");
        }
        if (polygonIndices.size() < 3) {
            throw new IllegalArgumentException("Polygon must contain at least three indices.");
        }

        List<Integer> remaining = new ArrayList<>(polygonIndices);
        List<int[]> triangles = new ArrayList<>();

        while (remaining.size() > 3) {
            boolean earFound = false;

            for (int i = 0; i < remaining.size(); i++) {
                int previousIndex = remaining.get((i - 1 + remaining.size()) % remaining.size());
                int currentIndex = remaining.get(i);
                int nextIndex = remaining.get((i + 1) % remaining.size());

                if (!isConvex(points, previousIndex, currentIndex, nextIndex)) {
                    continue;
                }

                if (containsAnyOtherVertex(points, remaining, previousIndex, currentIndex, nextIndex)) {
                    continue;
                }

                triangles.add(new int[]{previousIndex, currentIndex, nextIndex});
                remaining.remove(i);
                earFound = true;
                break;
            }

            if (!earFound) {
                throw new IllegalArgumentException("Failed to triangulate polygon using ear clipping.");
            }
        }

        triangles.add(new int[]{remaining.get(0), remaining.get(1), remaining.get(2)});
        return triangles;
    }

    List<Integer> buildNormalizedOuterIndices(List<Point2> outerPoints) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < outerPoints.size(); i++) {
            indices.add(i);
        }

        if (signedArea(outerPoints) < 0.0) {
            Collections.reverse(indices);
        }

        return indices;
    }

    List<Integer> buildNormalizedHoleIndices(List<Point2> holePoints, int offset) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < holePoints.size(); i++) {
            indices.add(offset + i);
        }

        if (signedArea(holePoints) > 0.0) {
            Collections.reverse(indices);
        }

        return indices;
    }

    List<Integer> bridgeSingleHoleIntoOuterLoop(
            List<Point2> allPoints,
            List<Point2> outerPoints,
            List<Point2> holePoints,
            List<Integer> outerIndices,
            List<Integer> holeIndices
    ) {
        int holeBridgePosition = findRightmostVertexPosition(allPoints, holeIndices);
        int holeBridgeIndex = holeIndices.get(holeBridgePosition);
        Point2 holeBridgePoint = allPoints.get(holeBridgeIndex);

        int chosenOuterPosition = -1;
        double bestDistanceSquared = Double.POSITIVE_INFINITY;

        for (int outerPosition = 0; outerPosition < outerIndices.size(); outerPosition++) {
            int outerIndex = outerIndices.get(outerPosition);
            Point2 outerPoint = allPoints.get(outerIndex);

            if (!isBridgeVisible(
                    holeBridgePoint,
                    outerPoint,
                    outerIndex,
                    holeBridgeIndex,
                    outerIndices,
                    holeIndices,
                    allPoints,
                    outerPoints,
                    holePoints
            )) {
                continue;
            }

            double distanceSquared = squaredDistance(holeBridgePoint, outerPoint);
            if (distanceSquared < bestDistanceSquared) {
                bestDistanceSquared = distanceSquared;
                chosenOuterPosition = outerPosition;
            }
        }

        if (chosenOuterPosition < 0) {
            throw new IllegalArgumentException("Failed to bridge polygon hole into outer loop.");
        }

        List<Integer> merged = new ArrayList<>();

        for (int i = 0; i <= chosenOuterPosition; i++) {
            merged.add(outerIndices.get(i));
        }

        merged.add(holeBridgeIndex);

        int holeSize = holeIndices.size();
        for (int step = 1; step < holeSize; step++) {
            int holeIndex = holeIndices.get((holeBridgePosition + step) % holeSize);
            merged.add(holeIndex);
        }

        merged.add(holeBridgeIndex);
        merged.add(outerIndices.get(chosenOuterPosition));

        for (int i = chosenOuterPosition + 1; i < outerIndices.size(); i++) {
            merged.add(outerIndices.get(i));
        }

        return merged;
    }

    int findRightmostVertexPosition(List<Point2> allPoints, List<Integer> indices) {
        int bestPosition = 0;
        Point2 bestPoint = allPoints.get(indices.getFirst());

        for (int i = 1; i < indices.size(); i++) {
            Point2 candidate = allPoints.get(indices.get(i));

            if (candidate.x() > bestPoint.x()) {
                bestPoint = candidate;
                bestPosition = i;
            } else if (candidate.x() == bestPoint.x() && candidate.y() < bestPoint.y()) {
                bestPoint = candidate;
                bestPosition = i;
            }
        }

        return bestPosition;
    }

    boolean isBridgeVisible(
            Point2 holePoint,
            Point2 outerPoint,
            int outerIndex,
            int holeIndex,
            List<Integer> outerIndices,
            List<Integer> holeIndices,
            List<Point2> allPoints,
            List<Point2> outerPoints,
            List<Point2> holePoints
    ) {
        if (segmentsIntersectImproperlyAgainstLoop(holePoint, outerPoint, outerIndices, allPoints, outerIndex, holeIndex)) {
            return false;
        }

        if (segmentsIntersectImproperlyAgainstLoop(holePoint, outerPoint, holeIndices, allPoints, outerIndex, holeIndex)) {
            return false;
        }

        Point2 midpoint = midpoint(holePoint, outerPoint);

        if (!isPointStrictlyInsidePolygon(midpoint, outerPoints)) {
            return false;
        }

        if (isPointStrictlyInsidePolygon(midpoint, holePoints)) {
            return false;
        }

        return true;
    }

    boolean segmentsIntersectImproperlyAgainstLoop(
            Point2 a,
            Point2 b,
            List<Integer> loopIndices,
            List<Point2> allPoints,
            int allowedOuterIndex,
            int allowedHoleIndex
    ) {
        for (int i = 0; i < loopIndices.size(); i++) {
            int cIndex = loopIndices.get(i);
            int dIndex = loopIndices.get((i + 1) % loopIndices.size());

            Point2 c = allPoints.get(cIndex);
            Point2 d = allPoints.get(dIndex);

            if (isSamePoint(a, c) || isSamePoint(a, d) || isSamePoint(b, c) || isSamePoint(b, d)) {
                continue;
            }

            if (segmentsIntersect(a, b, c, d)) {
                return true;
            }
        }

        return false;
    }

    boolean containsAnyOtherVertex(
            List<Point2> points,
            List<Integer> polygonIndices,
            int previousIndex,
            int currentIndex,
            int nextIndex
    ) {
        for (int candidateIndex : polygonIndices) {
            if (candidateIndex == previousIndex ||
                    candidateIndex == currentIndex ||
                    candidateIndex == nextIndex) {
                continue;
            }

            Point2 candidate = points.get(candidateIndex);
            if (isPointStrictlyInsideTriangle(
                    candidate,
                    points.get(previousIndex),
                    points.get(currentIndex),
                    points.get(nextIndex)
            )) {
                return true;
            }
        }

        return false;
    }

    boolean isConvex(List<Point2> points, int previousIndex, int currentIndex, int nextIndex) {
        Point2 a = points.get(previousIndex);
        Point2 b = points.get(currentIndex);
        Point2 c = points.get(nextIndex);

        return cross(a, b, c) > 0.0;
    }

    boolean isPointStrictlyInsideTriangle(Point2 p, Point2 a, Point2 b, Point2 c) {
        double abp = cross(a, b, p);
        double bcp = cross(b, c, p);
        double cap = cross(c, a, p);

        return abp > 0.0 && bcp > 0.0 && cap > 0.0;
    }

    boolean isPointStrictlyInsidePolygon(Point2 point, List<Point2> polygonPoints) {
        if (polygonPoints == null || polygonPoints.size() < 3) {
            return false;
        }

        for (int i = 0, j = polygonPoints.size() - 1; i < polygonPoints.size(); j = i++) {
            Point2 a = polygonPoints.get(j);
            Point2 b = polygonPoints.get(i);

            if (orientation(a, b, point) == 0.0 && onSegment(a, point, b)) {
                return false;
            }
        }

        boolean inside = false;

        for (int i = 0, j = polygonPoints.size() - 1; i < polygonPoints.size(); j = i++) {
            Point2 pi = polygonPoints.get(i);
            Point2 pj = polygonPoints.get(j);

            boolean intersects = ((pi.y() > point.y()) != (pj.y() > point.y())) &&
                    (point.x() < (pj.x() - pi.x()) * (point.y() - pi.y()) / (pj.y() - pi.y()) + pi.x());

            if (intersects) {
                inside = !inside;
            }
        }

        return inside;
    }

    boolean segmentsIntersect(Point2 p1, Point2 p2, Point2 q1, Point2 q2) {
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

    double signedArea(List<Point2> points) {
        double area = 0.0;

        for (int i = 0; i < points.size(); i++) {
            Point2 a = points.get(i);
            Point2 b = points.get((i + 1) % points.size());
            area += a.x() * b.y() - b.x() * a.y();
        }

        return area * 0.5;
    }

    double squaredDistance(Point2 a, Point2 b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        return dx * dx + dy * dy;
    }

    Point2 midpoint(Point2 a, Point2 b) {
        return new Point2((a.x() + b.x()) * 0.5, (a.y() + b.y()) * 0.5);
    }

    double cross(Point2 a, Point2 b, Point2 c) {
        return (b.x() - a.x()) * (c.y() - a.y()) -
                (b.y() - a.y()) * (c.x() - a.x());
    }

    double orientation(Point2 a, Point2 b, Point2 c) {
        return cross(a, b, c);
    }

    boolean onSegment(Point2 a, Point2 b, Point2 c) {
        return b.x() >= Math.min(a.x(), c.x()) &&
                b.x() <= Math.max(a.x(), c.x()) &&
                b.y() >= Math.min(a.y(), c.y()) &&
                b.y() <= Math.max(a.y(), c.y());
    }

    boolean isSamePoint(Point2 a, Point2 b) {
        return a.x() == b.x() && a.y() == b.y();
    }
}