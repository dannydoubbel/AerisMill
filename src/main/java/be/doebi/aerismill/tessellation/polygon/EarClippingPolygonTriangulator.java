package be.doebi.aerismill.tessellation.polygon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
            return triangulateSimplePolygon(
                    outerPoints,
                    buildNormalizedOuterIndices(outerPoints)
            );
        }

        for (PolygonLoop2 hole : polygon.holes()) {
            if (hole == null) {
                throw new IllegalArgumentException("Polygon hole loop must not be null.");
            }
            if (hole.points() == null) {
                throw new IllegalArgumentException("Polygon hole loop points must not be null.");
            }
            if (hole.points().size() < 3) {
                throw new IllegalArgumentException("Polygon hole loop must contain at least three points.");
            }
        }

        return triangulatePolygonWithHoles(polygon.outer(), polygon.holes());
    }

    List<int[]> triangulatePolygonWithHoles(PolygonLoop2 outer, List<PolygonLoop2> holes) {
        if (outer == null) {
            throw new IllegalArgumentException("Outer loop must not be null.");
        }
        if (outer.points() == null) {
            throw new IllegalArgumentException("Outer loop points must not be null.");
        }
        if (holes == null) {
            throw new IllegalArgumentException("Holes must not be null.");
        }

        List<Point2> allPoints = new ArrayList<>(outer.points());
        List<Integer> mergedIndices = buildNormalizedOuterIndices(outer.points());

        for (PolygonLoop2 hole : holes) {
            if (hole == null) {
                throw new IllegalArgumentException("Polygon hole loop must not be null.");
            }
            if (hole.points() == null) {
                throw new IllegalArgumentException("Polygon hole loop points must not be null.");
            }
            if (hole.points().size() < 3) {
                throw new IllegalArgumentException("Polygon hole loop must contain at least three points.");
            }

            int holeOffset = allPoints.size();
            allPoints.addAll(hole.points());

            List<Integer> holeIndices = buildNormalizedHoleIndices(hole.points(), holeOffset);

            List<Point2> currentOuterPoints = new ArrayList<>(mergedIndices.size());
            for (Integer index : mergedIndices) {
                currentOuterPoints.add(allPoints.get(index));
            }

            mergedIndices = bridgeSingleHoleIntoOuterLoop(
                    allPoints,
                    currentOuterPoints,
                    hole.points(),
                    mergedIndices,
                    holeIndices
            );
        }

        return triangulateSimplePolygon(allPoints, mergedIndices);
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

                if (isDegenerateEar(points, previousIndex, currentIndex, nextIndex)) {
                    continue;
                }

                triangles.add(new int[]{previousIndex, currentIndex, nextIndex});
                remaining.remove(i);
                earFound = true;
                break;
            }

            /*
            if (!earFound) {
                boolean removedCollinear = removeOneNearlyCollinearVertex(points, remaining);
                if (removedCollinear) {
                    continue;
                }

                throw new IllegalArgumentException("Failed to triangulate polygon using ear clipping.");
            }

             */
            if (!earFound) {
                throw new IllegalArgumentException("Failed to triangulate polygon using ear clipping.");
            }
        }

        int a = remaining.get(0);
        int b = remaining.get(1);
        int c = remaining.get(2);

        if (isDegenerateEar(points, a, b, c)) {
            throw new IllegalArgumentException(
                    "Final ear-clipping remainder is degenerate. "
                            + "remaining=" + remaining
                            + " | a=" + points.get(a)
                            + " b=" + points.get(b)
                            + " c=" + points.get(c)
            );
        }

        triangles.add(new int[]{a, b, c});
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


        List<OuterBridgeCandidate> candidates = new ArrayList<>();

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

            boolean rightSide = outerPoint.x() >= holeBridgePoint.x();
            double verticalPenalty = bridgeVerticalPenalty(holeBridgePoint, outerPoint);
            double distanceSquared = squaredDistance(holeBridgePoint, outerPoint);

            candidates.add(new OuterBridgeCandidate(
                    outerPosition,
                    rightSide,
                    verticalPenalty,
                    distanceSquared
            ));
        }

        candidates.sort(
                Comparator
                        .comparing((OuterBridgeCandidate candidate) -> !candidate.rightSide())
                        .thenComparingDouble(OuterBridgeCandidate::verticalPenalty)
                        .thenComparingDouble(OuterBridgeCandidate::distanceSquared)
        );

        for (OuterBridgeCandidate candidate : candidates) {
            List<Integer> merged = buildBridgedPolygonIndices(
                    outerIndices,
                    holeIndices,
                    candidate.outerPosition(),
                    holeBridgePosition
            );

            if (isSimpleMergedLoop(allPoints, merged, candidate.outerPosition(), holeIndices.size())) {
                return merged;
            }
        }
        throw new IllegalArgumentException(
                "Failed to bridge polygon hole into outer loop without creating self-intersection."
        );
    }

    boolean isSimpleMergedLoop(
            List<Point2> allPoints,
            List<Integer> mergedIndices,
            int outerBridgePosition,
            int holeSize
    ) {
        int edgeCount = mergedIndices.size();
        if (edgeCount < 3) {
            return false;
        }

        int firstBridgeEdgePosition = outerBridgePosition;
        int secondBridgeEdgePosition = outerBridgePosition + holeSize + 1;

        for (int i = 0; i < edgeCount; i++) {
            int iNext = (i + 1) % edgeCount;

            int aIndex = mergedIndices.get(i);
            int bIndex = mergedIndices.get(iNext);

            Point2 a = allPoints.get(aIndex);
            Point2 b = allPoints.get(bIndex);

            for (int j = i + 1; j < edgeCount; j++) {
                int jNext = (j + 1) % edgeCount;

                if (edgesAreAdjacent(i, iNext, j, jNext)) {
                    continue;
                }

                if (isDuplicateBridgeEdgePair(
                        i,
                        j,
                        firstBridgeEdgePosition,
                        secondBridgeEdgePosition
                )) {
                    continue;
                }

                int cIndex = mergedIndices.get(j);
                int dIndex = mergedIndices.get(jNext);

                if (sharesEndpoint(aIndex, bIndex, cIndex, dIndex)) {
                    continue;
                }

                Point2 c = allPoints.get(cIndex);
                Point2 d = allPoints.get(dIndex);

                if (segmentsIntersectInclusive(a, b, c, d)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean edgesAreAdjacent(int i, int iNext, int j, int jNext) {
        return i == j || i == jNext || iNext == j || iNext == jNext;
    }

    private boolean isDuplicateBridgeEdgePair(
            int edgeA,
            int edgeB,
            int firstBridgeEdgePosition,
            int secondBridgeEdgePosition
    ) {
        return (edgeA == firstBridgeEdgePosition && edgeB == secondBridgeEdgePosition)
                || (edgeA == secondBridgeEdgePosition && edgeB == firstBridgeEdgePosition);
    }

    private boolean segmentsIntersectInclusive(Point2 a, Point2 b, Point2 c, Point2 d) {
        double o1 = orientation(a, b, c);
        double o2 = orientation(a, b, d);
        double o3 = orientation(c, d, a);
        double o4 = orientation(c, d, b);

        if (hasOppositeSigns(o1, o2) && hasOppositeSigns(o3, o4)) {
            return true;
        }

        return isZero(o1) && onSegment(a, c, b)
                || isZero(o2) && onSegment(a, d, b)
                || isZero(o3) && onSegment(c, a, d)
                || isZero(o4) && onSegment(c, b, d);
    }

    private boolean hasOppositeSigns(double a, double b) {
        return (a > 0.0 && b < 0.0) || (a < 0.0 && b > 0.0);
    }

    private boolean isZero(double value) {
        return Math.abs(value) < 1.0e-12;
    }

    private boolean sharesEndpoint(int a, int b, int c, int d) {
        return a == c || a == d || b == c || b == d;
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

    private PolygonLoop2 mergeHolesIntoOuter(PolygonLoop2 outer, List<PolygonLoop2> holes) {
        PolygonLoop2 merged = outer;

        for (PolygonLoop2 hole : holes) {
            merged = mergeSingleHole(merged, hole);
        }

        return merged;
    }

    private PolygonLoop2 mergeSingleHole(PolygonLoop2 outer, PolygonLoop2 hole) {
        List<Point2> outerPoints = outer.points();
        List<Point2> holePoints = hole.points();

        if (outerPoints == null || outerPoints.isEmpty()) {
            throw new IllegalArgumentException("Outer loop must not be empty");
        }
        if (holePoints == null || holePoints.isEmpty()) {
            throw new IllegalArgumentException("Hole loop must not be empty");
        }

        List<Point2> allPoints = new ArrayList<>(outerPoints.size() + holePoints.size());
        allPoints.addAll(outerPoints);
        allPoints.addAll(holePoints);

        List<Integer> outerIndices = new ArrayList<>(outerPoints.size());
        for (int i = 0; i < outerPoints.size(); i++) {
            outerIndices.add(i);
        }

        List<Integer> holeIndices = buildNormalizedHoleIndices(holePoints, outerPoints.size());

        List<Integer> mergedIndices = bridgeSingleHoleIntoOuterLoop(
                allPoints,
                outerPoints,
                holePoints,
                outerIndices,
                holeIndices
        );

        List<Point2> mergedPoints = new ArrayList<>(mergedIndices.size());
        for (Integer index : mergedIndices) {
            mergedPoints.add(allPoints.get(index));
        }

        return new PolygonLoop2(mergedPoints);
    }

    private List<int[]> triangulateSimpleLoop(PolygonLoop2 loop) {
        List<Point2> points = loop.points();
        return triangulateSimplePolygon(points, buildNormalizedOuterIndices(points));
    }

    List<Integer> buildBridgedPolygonIndices(
            List<Integer> outerIndices,
            List<Integer> holeIndices,
            int outerBridgePosition,
            int holeBridgePosition
    ) {
        if (outerIndices == null || outerIndices.isEmpty()) {
            throw new IllegalArgumentException("Outer indices must not be null or empty.");
        }
        if (holeIndices == null || holeIndices.isEmpty()) {
            throw new IllegalArgumentException("Hole indices must not be null or empty.");
        }
        if (outerBridgePosition < 0 || outerBridgePosition >= outerIndices.size()) {
            throw new IllegalArgumentException("Outer bridge position is out of range.");
        }
        if (holeBridgePosition < 0 || holeBridgePosition >= holeIndices.size()) {
            throw new IllegalArgumentException("Hole bridge position is out of range.");
        }

        int outerSize = outerIndices.size();
        int holeSize = holeIndices.size();

        List<Integer> merged = new ArrayList<>(outerSize + holeSize + 2);

        // Outer loop up to and including the chosen outer bridge vertex.
        for (int i = 0; i <= outerBridgePosition; i++) {
            merged.add(outerIndices.get(i));
        }

        // Walk the hole starting at the chosen hole bridge vertex.
        for (int i = 0; i < holeSize; i++) {
            int holeIndex = (holeBridgePosition + i) % holeSize;
            merged.add(holeIndices.get(holeIndex));
        }

        // Repeat the hole bridge vertex to close the hole walk at the bridge.
        merged.add(holeIndices.get(holeBridgePosition));

        // Repeat the outer bridge vertex to return to the outer loop.
        merged.add(outerIndices.get(outerBridgePosition));

        // Continue the rest of the outer loop.
        for (int i = outerBridgePosition + 1; i < outerSize; i++) {
            merged.add(outerIndices.get(i));
        }

        return merged;
    }

    private PolygonLoop2 toLoop(List<Point2> allPoints, List<Integer> indices) {
        if (allPoints == null) {
            throw new IllegalArgumentException("All points must not be null.");
        }
        if (indices == null || indices.isEmpty()) {
            throw new IllegalArgumentException("Indices must not be null or empty.");
        }

        List<Point2> points = new ArrayList<>(indices.size());
        for (Integer index : indices) {
            if (index == null) {
                throw new IllegalArgumentException("Loop index must not be null.");
            }
            if (index < 0 || index >= allPoints.size()) {
                throw new IllegalArgumentException("Loop index out of bounds: " + index);
            }
            points.add(allPoints.get(index));
        }

        return new PolygonLoop2(points);
    }

    private double bridgeVerticalPenalty(Point2 holeBridgePoint, Point2 outerPoint) {
        return Math.abs(outerPoint.y() - holeBridgePoint.y());
    }

    private record OuterBridgeCandidate(
            int outerPosition,
            boolean rightSide,
            double verticalPenalty,
            double distanceSquared
    ) {}

    private boolean isDegenerateEar(
            List<Point2> points,
            int previousIndex,
            int currentIndex,
            int nextIndex
    ) {
        Point2 a = points.get(previousIndex);
        Point2 b = points.get(currentIndex);
        Point2 c = points.get(nextIndex);

        double twiceArea = Math.abs(cross(a, b, c));

        double ab = Math.hypot(b.x() - a.x(), b.y() - a.y());
        double bc = Math.hypot(c.x() - b.x(), c.y() - b.y());
        double ca = Math.hypot(a.x() - c.x(), a.y() - c.y());

        double scale = Math.max(ab + bc + ca, 1.0);
        double epsilon = 1.0e-9 * scale;

        return twiceArea <= epsilon;
    }

    private boolean removeOneNearlyCollinearVertex(List<Point2> points, List<Integer> remaining) {
        if (points == null || remaining == null || remaining.size() < 4) {
            return false;
        }

        for (int i = 0; i < remaining.size(); i++) {
            int previousIndex = remaining.get((i - 1 + remaining.size()) % remaining.size());
            int currentIndex = remaining.get(i);
            int nextIndex = remaining.get((i + 1) % remaining.size());

            if (isDegenerateEar(points, previousIndex, currentIndex, nextIndex)) {
                remaining.remove(i);
                return true;
            }
        }

        return false;
    }
}