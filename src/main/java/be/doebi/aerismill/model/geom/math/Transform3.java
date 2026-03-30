package be.doebi.aerismill.model.geom.math;

public final class Transform3 {
    private final double[][] m;

    public Transform3(double[][] m) {
        if (m == null || m.length != 4) {
            throw new IllegalArgumentException("Transform matrix must be 4x4");
        }
        for (double[] row : m) {
            if (row == null || row.length != 4) {
                throw new IllegalArgumentException("Transform matrix must be 4x4");
            }
        }

        this.m = copyMatrix(m);
    }

    public static Transform3 identity() {
        return new Transform3(new double[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        });
    }

    public static Transform3 fromFrame(Frame3 frame) {
        Point3 o = frame.origin();
        Vec3 x = frame.xAxis().toVec3();
        Vec3 y = frame.yAxis().toVec3();
        Vec3 z = frame.zAxis().toVec3();

        return new Transform3(new double[][]{
                {x.x(), y.x(), z.x(), o.x()},
                {x.y(), y.y(), z.y(), o.y()},
                {x.z(), y.z(), z.z(), o.z()},
                {0,     0,     0,     1}
        });
    }

    public Point3 transformPoint(Point3 p) {
        double x = m[0][0] * p.x() + m[0][1] * p.y() + m[0][2] * p.z() + m[0][3];
        double y = m[1][0] * p.x() + m[1][1] * p.y() + m[1][2] * p.z() + m[1][3];
        double z = m[2][0] * p.x() + m[2][1] * p.y() + m[2][2] * p.z() + m[2][3];
        double w = m[3][0] * p.x() + m[3][1] * p.y() + m[3][2] * p.z() + m[3][3];

        if (w == 0.0) {
            throw new IllegalStateException("Transformed point has w=0");
        }

        if (w != 1.0) {
            return new Point3(x / w, y / w, z / w);
        }

        return new Point3(x, y, z);
    }

    public Vec3 transformVector(Vec3 v) {
        double x = m[0][0] * v.x() + m[0][1] * v.y() + m[0][2] * v.z();
        double y = m[1][0] * v.x() + m[1][1] * v.y() + m[1][2] * v.z();
        double z = m[2][0] * v.x() + m[2][1] * v.y() + m[2][2] * v.z();

        return new Vec3(x, y, z);
    }

    public UnitVec3 transformDirection(UnitVec3 d) {
        Vec3 transformed = transformVector(d.toVec3());
        return UnitVec3.of(transformed);
    }

    public Transform3 combine(Transform3 other) {
        double[][] result = new double[4][4];

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double sum = 0.0;
                for (int k = 0; k < 4; k++) {
                    sum += this.m[row][k] * other.m[k][col];
                }
                result[row][col] = sum;
            }
        }

        return new Transform3(result);
    }

    public Transform3 inverse() {
        // Assumes rigid affine transform:
        // upper-left 3x3 = orthonormal rotation
        // last row = [0 0 0 1]
        double[][] result = new double[4][4];

        // R^-1 = R^T
        result[0][0] = m[0][0];
        result[0][1] = m[1][0];
        result[0][2] = m[2][0];

        result[1][0] = m[0][1];
        result[1][1] = m[1][1];
        result[1][2] = m[2][1];

        result[2][0] = m[0][2];
        result[2][1] = m[1][2];
        result[2][2] = m[2][2];

        double tx = m[0][3];
        double ty = m[1][3];
        double tz = m[2][3];

        result[0][3] = -(result[0][0] * tx + result[0][1] * ty + result[0][2] * tz);
        result[1][3] = -(result[1][0] * tx + result[1][1] * ty + result[1][2] * tz);
        result[2][3] = -(result[2][0] * tx + result[2][1] * ty + result[2][2] * tz);

        result[3][0] = 0;
        result[3][1] = 0;
        result[3][2] = 0;
        result[3][3] = 1;

        return new Transform3(result);
    }

    public double[][] getMatrix() {
        return copyMatrix(m);
    }

    private static double[][] copyMatrix(double[][] source) {
        double[][] copy = new double[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();
        }
        return copy;
    }
}