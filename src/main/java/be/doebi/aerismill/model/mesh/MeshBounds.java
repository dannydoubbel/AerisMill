package be.doebi.aerismill.model.mesh;

import be.doebi.aerismill.model.geom.math.Point3;

public record MeshBounds(
        double minX,
        double minY,
        double minZ,
        double maxX,
        double maxY,
        double maxZ
) {
    public double sizeX() {
        return maxX - minX;
    }

    public double sizeY() {
        return maxY - minY;
    }

    public double sizeZ() {
        return maxZ - minZ;
    }

    public double maxSpan() {
        return Math.max(sizeX(), Math.max(sizeY(), sizeZ()));
    }

    public Point3 center() {
        return new Point3(
                (minX + maxX) * 0.5,
                (minY + maxY) * 0.5,
                (minZ + maxZ) * 0.5
        );
    }

    public double diagonal() {
        double dx = sizeX();
        double dy = sizeY();
        double dz = sizeZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}