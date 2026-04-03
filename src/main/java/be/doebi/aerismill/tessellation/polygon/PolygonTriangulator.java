package be.doebi.aerismill.tessellation.polygon;

import java.util.List;

public interface PolygonTriangulator {
    List<int[]> triangulate(PolygonWithHoles2 polygon);
}
