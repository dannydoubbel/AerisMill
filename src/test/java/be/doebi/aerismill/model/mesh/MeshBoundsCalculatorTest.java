package be.doebi.aerismill.model.mesh;

import be.doebi.aerismill.model.geom.math.Point3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeshBoundsCalculatorTest {

    @Test
    void calculate_returnsBoundsForMultipleVertices() {
        List<MeshVertex> vertices = List.of(
                vertex(0, 10, 20, 30),
                vertex(1, -5, 40, 15),
                vertex(2, 7, -2, 99)
        );

        MeshBounds bounds = MeshBoundsCalculator.calculate(vertices);

        assertEquals(-5, bounds.minX());
        assertEquals(-2, bounds.minY());
        assertEquals(15, bounds.minZ());

        assertEquals(10, bounds.maxX());
        assertEquals(40, bounds.maxY());
        assertEquals(99, bounds.maxZ());
    }

    @Test
    void calculate_returnsZeroSizedBoundsForSingleVertex() {
        List<MeshVertex> vertices = List.of(
                vertex(0, 3, 4, 5)
        );

        MeshBounds bounds = MeshBoundsCalculator.calculate(vertices);

        assertEquals(3, bounds.minX());
        assertEquals(4, bounds.minY());
        assertEquals(5, bounds.minZ());

        assertEquals(3, bounds.maxX());
        assertEquals(4, bounds.maxY());
        assertEquals(5, bounds.maxZ());

        assertEquals(0, bounds.sizeX());
        assertEquals(0, bounds.sizeY());
        assertEquals(0, bounds.sizeZ());
    }

    @Test
    void calculate_throwsForEmptyVertexList() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> MeshBoundsCalculator.calculate(List.of())
        );

        assertEquals("Cannot calculate bounds for empty vertex list", ex.getMessage());
    }

    @Test
    void calculate_throwsForNonFiniteCoordinate() {
        List<MeshVertex> vertices = List.of(
                vertex(0, 1, 2, 3),
                vertex(1, Double.NaN, 4, 5)
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> MeshBoundsCalculator.calculate(vertices)
        );

        assertTrue(ex.getMessage().contains("Vertex contains non-finite coordinate"));
    }

    private static MeshVertex vertex(int index, double x, double y, double z) {
        return new MeshVertex(index, new Point3(x, y, z));
    }
}