package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Point3;
import java.util.List;

public record FaceMeshPatch(
        List<Point3> vertices,
        List<int[]> triangles,
        SurfaceFamily surfaceFamily
) {}