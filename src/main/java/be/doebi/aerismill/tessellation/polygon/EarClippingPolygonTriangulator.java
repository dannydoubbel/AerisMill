package be.doebi.aerismill.tessellation.polygon;

import java.util.ArrayList;
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
        if (!polygon.holes().isEmpty()) {
            throw new UnsupportedOperationException("Polygon holes are not supported yet.");
        }

        List<Point2> points = polygon.outer().points();
        if (points == null) {
            throw new IllegalArgumentException("Polygon outer loop points must not be null.");
        }
        if (points.size() < 3) {
            throw new IllegalArgumentException("Polygon outer loop must contain at least three points.");
        }

        List<Integer> vertexIndices = buildWindingNormalizedIndexList(points);
        List<int[]> triangles = new ArrayList<>();

        while (vertexIndices.size() > 3) {
            boolean earFound = false;

            for (int i = 0; i < vertexIndices.size(); i++) {
                int previousIndex = vertexIndices.get((i - 1 + vertexIndices.size()) % vertexIndices.size());
                int currentIndex = vertexIndices.get(i);
                int nextIndex = vertexIndices.get((i + 1) % vertexIndices.size());

                if (!isConvex(points, previousIndex, currentIndex, nextIndex)) {
                    continue;
                }

                if (containsAnyOtherVertex(points, vertexIndices, previousIndex, currentIndex, nextIndex)) {
                    continue;
                }

                triangles.add(new int[]{previousIndex, currentIndex, nextIndex});
                vertexIndices.remove(i);
                earFound = true;
                break;
            }

            if (!earFound) {
                throw new IllegalArgumentException("Failed to triangulate polygon using ear clipping.");
            }
        }

        triangles.add(new int[]{
                vertexIndices.get(0),
                vertexIndices.get(1),
                vertexIndices.get(2)
        });

        return triangles;
    }

    List<Integer> buildWindingNormalizedIndexList(List<Point2> points) {
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            indices.add(i);
        }

        if (signedArea(points) < 0.0) {
            List<Integer> reversed = new ArrayList<>();
            for (int i = indices.size() - 1; i >= 0; i--) {
                reversed.add(indices.get(i));
            }
            return reversed;
        }

        return indices;
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

    boolean isConvex(List<Point2> points, int previousIndex, int currentIndex, int nextIndex) {
        Point2 a = points.get(previousIndex);
        Point2 b = points.get(currentIndex);
        Point2 c = points.get(nextIndex);

        return cross(a, b, c) > 0.0;
    }

    boolean containsAnyOtherVertex(
            List<Point2> points,
            List<Integer> vertexIndices,
            int previousIndex,
            int currentIndex,
            int nextIndex
    ) {
        for (int candidateIndex : vertexIndices) {
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

    boolean isPointStrictlyInsideTriangle(Point2 p, Point2 a, Point2 b, Point2 c) {
        double abp = cross(a, b, p);
        double bcp = cross(b, c, p);
        double cap = cross(c, a, p);

        return abp > 0.0 && bcp > 0.0 && cap > 0.0;
    }

    double cross(Point2 a, Point2 b, Point2 c) {
        return (b.x() - a.x()) * (c.y() - a.y()) -
                (b.y() - a.y()) * (c.x() - a.x());
    }
}