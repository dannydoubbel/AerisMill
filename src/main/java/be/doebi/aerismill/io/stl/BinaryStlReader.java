package be.doebi.aerismill.io.stl;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class BinaryStlReader {

    private static final int HEADER_SIZE = 80;
    private static final int TRIANGLE_COUNT_SIZE = 4;
    private static final int TRIANGLE_RECORD_SIZE = 50;

    public Mesh read(Path path) throws IOException {
        Objects.requireNonNull(path, "path must not be null");

        byte[] bytes = Files.readAllBytes(path);
        if (bytes.length < HEADER_SIZE + TRIANGLE_COUNT_SIZE) {
            throw new IllegalArgumentException("Binary STL is too short to contain header and triangle count");
        }

        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);

        buffer.position(HEADER_SIZE);
        long triangleCount = Integer.toUnsignedLong(buffer.getInt());

        long expectedSize = HEADER_SIZE + TRIANGLE_COUNT_SIZE + triangleCount * TRIANGLE_RECORD_SIZE;
        if (bytes.length != expectedSize) {
            throw new IllegalArgumentException(
                    "Binary STL size mismatch. Expected " + expectedSize + " bytes but found " + bytes.length
            );
        }

        List<MeshVertex> vertices = new ArrayList<>();
        List<MeshTriangle> triangles = new ArrayList<>();
        Map<Point3, Integer> indexByPoint = new LinkedHashMap<>();

        for (long i = 0; i < triangleCount; i++) {
            // normal vector (ignored for now)
            float nx = buffer.getFloat();
            float ny = buffer.getFloat();
            float nz = buffer.getFloat();
            validateFinite(nx, ny, nz, "normal", i);

            Point3 a = readPoint(buffer, i, "vertex A");
            Point3 b = readPoint(buffer, i, "vertex B");
            Point3 c = readPoint(buffer, i, "vertex C");

            if (a.equals(b) || b.equals(c) || a.equals(c)) {
                throw new IllegalArgumentException("Binary STL triangle " + i + " contains duplicate vertex positions");
            }

            int attributeByteCount = Short.toUnsignedInt(buffer.getShort());
            // ignored for now, but read so buffer stays aligned
            if (attributeByteCount < 0) {
                throw new IllegalArgumentException("Invalid attribute byte count in triangle " + i);
            }

            int ia = vertexIndex(a, indexByPoint, vertices);
            int ib = vertexIndex(b, indexByPoint, vertices);
            int ic = vertexIndex(c, indexByPoint, vertices);

            triangles.add(new MeshTriangle(ia, ib, ic));
        }

        return new Mesh(vertices, triangles);
    }

    private static Point3 readPoint(ByteBuffer buffer, long triangleIndex, String label) {
        float x = buffer.getFloat();
        float y = buffer.getFloat();
        float z = buffer.getFloat();

        validateFinite(x, y, z, label, triangleIndex);
        return new Point3(x, y, z);
    }

    private static void validateFinite(float x, float y, float z, String label, long triangleIndex) {
        if (!Float.isFinite(x) || !Float.isFinite(y) || !Float.isFinite(z)) {
            throw new IllegalArgumentException(
                    "Binary STL triangle " + triangleIndex + " contains non-finite " + label + " coordinate(s)"
            );
        }
    }

    private static int vertexIndex(
            Point3 point,
            Map<Point3, Integer> indexByPoint,
            List<MeshVertex> vertices
    ) {
        Integer existing = indexByPoint.get(point);
        if (existing != null) {
            return existing;
        }

        int index = vertices.size();
        vertices.add(new MeshVertex(index, point));
        indexByPoint.put(point, index);
        return index;
    }
}