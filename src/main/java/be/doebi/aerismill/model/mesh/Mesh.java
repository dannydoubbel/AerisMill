package be.doebi.aerismill.model.mesh;

import java.util.List;

public record Mesh(
        List<MeshVertex> vertices,
        List<MeshTriangle> triangles
) {}