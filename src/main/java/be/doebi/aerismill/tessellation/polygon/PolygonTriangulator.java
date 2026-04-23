package be.doebi.aerismill.tessellation.polygon;

import be.doebi.aerismill.tessellation.face.PlanarFaceTessellator;

import java.util.List;

public interface PolygonTriangulator {
    PlanarFaceTessellator.TriangulationResult triangulateWithPoints(PolygonWithHoles2 polygon);

    default List<int[]> triangulate(PolygonWithHoles2 polygon) {
        return triangulateWithPoints(polygon).triangles();
    }
}
