package be.doebi.aerismill.model.mesh;

import be.doebi.aerismill.model.geom.math.Point3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MeshBoundsTest {

    @Test
    void center_returnsMiddlePoint() {
        MeshBounds bounds = new MeshBounds(
                -10, 20, 30,
                30, 60, 50
        );

        Point3 center = bounds.center();

        assertEquals(10, center.x());
        assertEquals(40, center.y());
        assertEquals(40, center.z());
    }

    @Test
    void diagonal_returnsBoundingBoxDiagonalLength() {
        MeshBounds bounds = new MeshBounds(
                0, 0, 0,
                3, 4, 12
        );

        assertEquals(13.0, bounds.diagonal());
    }

    @Test
    void maxSpan_returnsLargestAxisSize() {
        MeshBounds bounds = new MeshBounds(
                0, 0, 0,
                3, 7, 5
        );

        assertEquals(7.0, bounds.maxSpan());
    }
    @Test
    void sizeMethods_returnAxisSpans() {
        MeshBounds bounds = new MeshBounds(
                -2, 10, 4,
                8, 16, 19
        );

        assertEquals(10.0, bounds.sizeX());
        assertEquals(6.0, bounds.sizeY());
        assertEquals(15.0, bounds.sizeZ());
    }
}