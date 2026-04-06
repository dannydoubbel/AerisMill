package be.doebi.aerismill.model.mesh;

import be.doebi.aerismill.model.geom.math.Point3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeshTest {

    @Test
    void bounds_returnsCalculatedBoundsFromVertices() {
        Mesh mesh = new Mesh(
                List.of(
                        new MeshVertex(0, new Point3(-1, 2, 3)),
                        new MeshVertex(1, new Point3(5, 6, 7)),
                        new MeshVertex(2, new Point3(0, -4, 10))
                ),
                List.of()
        );

        MeshBounds bounds = mesh.bounds();

        assertEquals(-1, bounds.minX());
        assertEquals(-4, bounds.minY());
        assertEquals(3, bounds.minZ());
        assertEquals(5, bounds.maxX());
        assertEquals(6, bounds.maxY());
        assertEquals(10, bounds.maxZ());
    }

    @Test
    void isEmpty_returnsTrueWhenNoTriangles() {
        Mesh mesh = new Mesh(
                List.of(
                        new MeshVertex(0, new Point3(0, 0, 0))
                ),
                List.of()
        );

        assertTrue(mesh.isEmpty());
    }
}