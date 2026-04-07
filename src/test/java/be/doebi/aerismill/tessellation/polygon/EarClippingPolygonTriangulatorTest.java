package be.doebi.aerismill.tessellation.polygon;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EarClippingPolygonTriangulatorTest {

    @Test
    void triangulate_triangle_returnsSingleTriangle() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        PolygonWithHoles2 polygon = polygon(
                p(0, 0),
                p(1, 0),
                p(0, 1)
        );

        List<int[]> triangles = triangulator.triangulate(polygon);

        assertEquals(1, triangles.size());
        assertTriangleReferencesDistinctVertices(triangles.get(0));
    }

    @Test
    void triangulate_convexQuad_returnsTwoTriangles() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        PolygonWithHoles2 polygon = polygon(
                p(0, 0),
                p(2, 0),
                p(2, 1),
                p(0, 1)
        );

        List<int[]> triangles = triangulator.triangulate(polygon);

        assertEquals(2, triangles.size());
        assertAllTrianglesReferenceDistinctVertices(triangles);
    }

    @Test
    void triangulate_concavePolygon_returnsExpectedTriangleCount() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        PolygonWithHoles2 polygon = polygon(
                p(0, 0),
                p(2, 0),
                p(2, 2),
                p(1, 1),
                p(0, 2)
        );

        List<int[]> triangles = triangulator.triangulate(polygon);

        assertEquals(3, triangles.size());
        assertAllTrianglesReferenceDistinctVertices(triangles);
    }

    @Test
    void triangulate_handlesClockwiseOuterLoop() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        PolygonWithHoles2 polygon = polygon(
                p(0, 0),
                p(0, 1),
                p(2, 1),
                p(2, 0)
        );

        List<int[]> triangles = triangulator.triangulate(polygon);

        assertEquals(2, triangles.size());
        assertAllTrianglesReferenceDistinctVertices(triangles);
    }

    @Test
    void triangulate_rejectsPolygonWithHoles() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        PolygonWithHoles2 polygon = new PolygonWithHoles2(
                new PolygonLoop2(List.of(
                        p(0, 0), p(4, 0), p(4, 4), p(0, 4)
                )),
                List.of(
                        new PolygonLoop2(List.of(
                                p(1, 1), p(2, 1), p(2, 2), p(1, 2)
                        ))
                )
        );

        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> triangulator.triangulate(polygon)
        );

        assertEquals("Polygon holes are not supported yet.", ex.getMessage());
    }

    @Test
    void triangulate_rejectsNullPolygon() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> triangulator.triangulate(null)
        );

        assertEquals("Polygon must not be null.", ex.getMessage());
    }

    @Test
    void triangulate_rejectsOuterLoopWithTooFewPoints() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        PolygonWithHoles2 polygon = polygon(
                p(0, 0),
                p(1, 0)
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> triangulator.triangulate(polygon)
        );

        assertEquals("Polygon outer loop must contain at least three points.", ex.getMessage());
    }

    private PolygonWithHoles2 polygon(Point2... points) {
        return new PolygonWithHoles2(
                new PolygonLoop2(List.of(points)),
                List.of()
        );
    }

    private Point2 p(double x, double y) {
        return new Point2(x, y);
    }

    private void assertAllTrianglesReferenceDistinctVertices(List<int[]> triangles) {
        for (int[] triangle : triangles) {
            assertTriangleReferencesDistinctVertices(triangle);
        }
    }

    private void assertTriangleReferencesDistinctVertices(int[] triangle) {
        assertNotNull(triangle);
        assertEquals(3, triangle.length);
        assertNotEquals(triangle[0], triangle[1]);
        assertNotEquals(triangle[0], triangle[2]);
        assertNotEquals(triangle[1], triangle[2]);
    }
}