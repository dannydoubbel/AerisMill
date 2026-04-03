package be.doebi.aerismill.tessellation.polygon;

import java.util.List;

public record PolygonWithHoles2(
        PolygonLoop2 outer,
        List<PolygonLoop2> holes
) {}