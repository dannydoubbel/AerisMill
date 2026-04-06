package be.doebi.aerismill.model.mesh;

import java.util.List;
import java.util.Objects;

public record Mesh(
        List<MeshVertex> vertices,
        List<MeshTriangle> triangles
) {
    public Mesh {
        Objects.requireNonNull(vertices, "vertices must not be null");
        Objects.requireNonNull(triangles, "triangles must not be null");

        vertices = List.copyOf(vertices);
        triangles = List.copyOf(triangles);
    }

    public MeshBounds bounds() {
        return MeshBoundsCalculator.calculate(vertices);
    }

    public boolean isEmpty() {
        return vertices.isEmpty() || triangles.isEmpty();
    }

    public int vertexCount() {
        return vertices.size();
    }

    public int triangleCount() {
        return triangles.size();
    }
}