package be.doebi.aerismill.model.geom.math;

public record Point3(double x, double y, double z) {

    public Point3 add(Vec3 v) {
        return new Point3(
                x + v.x(),
                y + v.y(),
                z + v.z()
        );
    }

    public Vec3 subtract(Point3 other) {
        return new Vec3(
                x - other.x(),
                y - other.y(),
                z - other.z()
        );
    }
}
