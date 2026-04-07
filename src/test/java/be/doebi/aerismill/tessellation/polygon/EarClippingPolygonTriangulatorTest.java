package be.doebi.aerismill.tessellation.polygon;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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

    @Test
    void buildBridgedPolygonIndices_repeatsBridgeVerticesInExpectedOrder() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        List<Integer> outer = List.of(0, 1, 2, 3);
        List<Integer> hole = List.of(4, 5, 6);

        List<Integer> result = triangulator.buildBridgedPolygonIndices(
                outer,
                hole,
                1,
                0
        );

        assertEquals(List.of(0, 1, 4, 5, 6, 4, 1, 2, 3), result);
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

    @Test
    void triangulate_singleHoleSquare_returnsTriangles() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        PolygonWithHoles2 polygon = new PolygonWithHoles2(
                new PolygonLoop2(List.of(
                        p(0, 0),
                        p(6, 0),
                        p(6, 6),
                        p(0, 6)
                )),
                List.of(
                        new PolygonLoop2(List.of(
                                p(2, 2),
                                p(2, 4),
                                p(4, 4),
                                p(4, 2)
                        ))
                )
        );

        List<int[]> triangles = triangulator.triangulate(polygon);

        assertEquals(8, triangles.size());
        assertAllTrianglesReferenceDistinctVertices(triangles);

        for (int[] triangle : triangles) {
            for (int index : triangle) {
                assertTrue(index >= 0 && index < 8);
            }
        }
    }

    @Test
    void triangulate_rejectsMultipleHoles() {
        EarClippingPolygonTriangulator triangulator = new EarClippingPolygonTriangulator();

        PolygonWithHoles2 polygon = new PolygonWithHoles2(
                new PolygonLoop2(List.of(
                        p(0, 0), p(8, 0), p(8, 8), p(0, 8)
                )),
                List.of(
                        new PolygonLoop2(List.of(
                                p(1, 1), p(1, 2), p(2, 2), p(2, 1)
                        )),
                        new PolygonLoop2(List.of(
                                p(5, 5), p(5, 6), p(6, 6), p(6, 5)
                        ))
                )
        );

        UnsupportedOperationException ex = assertThrows(
                UnsupportedOperationException.class,
                () -> triangulator.triangulate(polygon)
        );

        assertEquals("Multiple polygon holes are not supported yet.", ex.getMessage());
    }

    @Test
    void bridgeSingleHoleIntoOuterLoop_triesAlternateVisibleBridgeWhenNearestCreatesSelfIntersection() {
        ScriptedEarClippingPolygonTriangulator triangulator =
                new ScriptedEarClippingPolygonTriangulator(
                        java.util.Set.of(0, 1),
                        java.util.Set.of(1)
                );

        List<Point2> outerPoints = bridgeTestOuterPoints();
        List<Point2> holePoints = bridgeTestHolePoints();
        List<Point2> allPoints = bridgeTestAllPoints();

        List<Integer> outerIndices = List.of(0, 1, 2, 3);
        List<Integer> holeIndices = List.of(4, 5, 6);

        List<Integer> result = triangulator.bridgeSingleHoleIntoOuterLoop(
                allPoints,
                outerPoints,
                holePoints,
                outerIndices,
                holeIndices
        );

        assertEquals(List.of(0, 1, 4, 5, 6, 4, 1, 2, 3), result);
        assertEquals(List.of(0, 1), triangulator.attemptedOuterPositions());
    }

    @Test
    void bridgeSingleHoleIntoOuterLoop_throwsWhenNoValidNonSelfIntersectingBridgeExists() {
        ScriptedEarClippingPolygonTriangulator triangulator =
                new ScriptedEarClippingPolygonTriangulator(
                        java.util.Set.of(0, 1),
                        java.util.Set.of()
                );

        List<Point2> outerPoints = bridgeTestOuterPoints();
        List<Point2> holePoints = bridgeTestHolePoints();
        List<Point2> allPoints = bridgeTestAllPoints();

        List<Integer> outerIndices = List.of(0, 1, 2, 3);
        List<Integer> holeIndices = List.of(4, 5, 6);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> triangulator.bridgeSingleHoleIntoOuterLoop(
                        allPoints,
                        outerPoints,
                        holePoints,
                        outerIndices,
                        holeIndices
                )
        );

        assertEquals(
                "Failed to bridge polygon hole into outer loop without creating self-intersection.",
                ex.getMessage()
        );
        assertEquals(List.of(0, 1), triangulator.attemptedOuterPositions());
    }



    private static final class ScriptedEarClippingPolygonTriangulator extends EarClippingPolygonTriangulator {

        private final java.util.Set<Integer> visibleOuterPositions;
        private final java.util.Set<Integer> validOuterPositions;
        private final java.util.List<Integer> attemptedOuterPositions = new java.util.ArrayList<>();

        private ScriptedEarClippingPolygonTriangulator(
                java.util.Set<Integer> visibleOuterPositions,
                java.util.Set<Integer> validOuterPositions
        ) {
            this.visibleOuterPositions = visibleOuterPositions;
            this.validOuterPositions = validOuterPositions;
        }

        java.util.List<Integer> attemptedOuterPositions() {
            return attemptedOuterPositions;
        }

        @Override
        boolean isBridgeVisible(
                Point2 holeBridgePoint,
                Point2 outerPoint,
                int outerIndex,
                int holeBridgeIndex,
                List<Integer> outerIndices,
                List<Integer> holeIndices,
                List<Point2> allPoints,
                List<Point2> outerPoints,
                List<Point2> holePoints
        ) {
            int outerPosition = outerIndices.indexOf(outerIndex);
            return visibleOuterPositions.contains(outerPosition);
        }

        @Override
        boolean isSimpleMergedLoop(
                List<Point2> allPoints,
                List<Integer> mergedIndices,
                int outerBridgePosition,
                int holeSize
        ) {
            attemptedOuterPositions.add(outerBridgePosition);
            return validOuterPositions.contains(outerBridgePosition);
        }
    }

    private List<Point2> bridgeTestOuterPoints() {
        return List.of(
                new Point2(6.0, 5.0),  // outer position 0 -> nearest
                new Point2(8.0, 5.0),  // outer position 1 -> second nearest
                new Point2(0.0, 10.0),
                new Point2(0.0, 0.0)
        );
    }

    private List<Point2> bridgeTestHolePoints() {
        return List.of(
                new Point2(5.0, 5.0),  // rightmost hole vertex -> hole bridge
                new Point2(4.0, 6.0),
                new Point2(3.0, 4.0)
        );
    }

    private List<Point2> bridgeTestAllPoints() {
        List<Point2> all = new ArrayList<>();
        all.addAll(bridgeTestOuterPoints());
        all.addAll(bridgeTestHolePoints());
        return all;
    }


}