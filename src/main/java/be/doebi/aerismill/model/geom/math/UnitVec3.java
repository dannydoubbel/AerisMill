package be.doebi.aerismill.model.geom.math;

public record UnitVec3(double x, double y, double z) {

    public static UnitVec3 of(Vec3 v) {
        if (v == null) {
            throw new IllegalArgumentException("Vector cannot be null");
        }

        double len = v.length();
        if (len == 0.0) {
            throw new IllegalArgumentException("Cannot create UnitVec3 from zero-length vector");
        }

        return new UnitVec3(
                v.x() / len,
                v.y() / len,
                v.z() / len
        );
    }

    public Vec3 toVec3() {
        return new Vec3(x, y, z);
    }
}