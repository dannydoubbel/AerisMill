package be.doebi.aerismill.model.mesh;

import be.doebi.aerismill.model.geom.math.Point3;

import java.util.List;
import java.util.Objects;

public final class MeshBoundsCalculator {

    private MeshBoundsCalculator() {
    }

    public static MeshBounds calculate(List<MeshVertex> vertices) {
        Objects.requireNonNull(vertices, "vertices must not be null");

        if (vertices.isEmpty()) {
            throw new IllegalArgumentException("Cannot calculate bounds for empty vertex list");
        }

        Point3 first = vertices.get(0).point();
        validateFinite(first);

        double minX = first.x();
        double minY = first.y();
        double minZ = first.z();
        double maxX = first.x();
        double maxY = first.y();
        double maxZ = first.z();

        for (int i = 1; i < vertices.size(); i++) {
            Point3 p = vertices.get(i).point();
            validateFinite(p);

            minX = Math.min(minX, p.x());
            minY = Math.min(minY, p.y());
            minZ = Math.min(minZ, p.z());

            maxX = Math.max(maxX, p.x());
            maxY = Math.max(maxY, p.y());
            maxZ = Math.max(maxZ, p.z());
        }

        return new MeshBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }

    private static void validateFinite(Point3 point) {
        if (!Double.isFinite(point.x())
                || !Double.isFinite(point.y())
                || !Double.isFinite(point.z())) {
            throw new IllegalArgumentException("Vertex contains non-finite coordinate: " + point);
        }
    }
}