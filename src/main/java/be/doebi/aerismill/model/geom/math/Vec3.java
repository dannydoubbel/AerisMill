package be.doebi.aerismill.model.geom.math;

public record Vec3(double x, double y, double z) {

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vec3 normalize() {
        double len = length();
        if (len == 0.0) {
            throw new IllegalStateException("Cannot normalize zero-length vector");
        }
        return new Vec3(x / len, y / len, z / len);
    }

    public double dot(Vec3 other) {
        return x * other.x() + y * other.y() + z * other.z();
    }

    public Vec3 cross(Vec3 other) {
        return new Vec3(
                y * other.z() - z * other.y(),
                z * other.x() - x * other.z(),
                x * other.y() - y * other.x()
        );
    }

    public Vec3 scale(double s) {
        return new Vec3(x * s, y * s, z * s);
    }

    public Vec3 add(Vec3 other) {
        return new Vec3(
                x + other.x(),
                y + other.y(),
                z + other.z()
        );
    }
}