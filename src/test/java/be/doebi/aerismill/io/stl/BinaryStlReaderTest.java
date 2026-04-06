package be.doebi.aerismill.io.stl;

import be.doebi.aerismill.model.mesh.Mesh;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BinaryStlReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void read_singleTriangle_returnsMesh() throws IOException {
        Path file = tempDir.resolve("single_triangle_binary.stl");
        Files.write(file, binaryStl(
                triangle(
                        0f, 0f, 1f,
                        0f, 0f, 0f,
                        1f, 0f, 0f,
                        0f, 1f, 0f
                )
        ));

        BinaryStlReader reader = new BinaryStlReader();
        Mesh mesh = reader.read(file);

        assertEquals(3, mesh.vertexCount());
        assertEquals(1, mesh.triangleCount());

        assertEquals(0.0, mesh.bounds().minX());
        assertEquals(0.0, mesh.bounds().minY());
        assertEquals(0.0, mesh.bounds().minZ());

        assertEquals(1.0, mesh.bounds().maxX());
        assertEquals(1.0, mesh.bounds().maxY());
        assertEquals(0.0, mesh.bounds().maxZ());
    }

    @Test
    void read_twoTrianglesSquare_deduplicatesSharedVertices() throws IOException {
        Path file = tempDir.resolve("square_binary.stl");
        Files.write(file, binaryStl(
                triangle(
                        0f, 0f, 1f,
                        0f, 0f, 0f,
                        1f, 0f, 0f,
                        1f, 1f, 0f
                ),
                triangle(
                        0f, 0f, 1f,
                        0f, 0f, 0f,
                        1f, 1f, 0f,
                        0f, 1f, 0f
                )
        ));

        BinaryStlReader reader = new BinaryStlReader();
        Mesh mesh = reader.read(file);

        assertEquals(4, mesh.vertexCount());
        assertEquals(2, mesh.triangleCount());
    }

    @Test
    void read_throwsWhenFileTooShort() throws IOException {
        Path file = tempDir.resolve("too_short.stl");
        Files.write(file, new byte[10]);

        BinaryStlReader reader = new BinaryStlReader();

        assertThrows(IllegalArgumentException.class, () -> reader.read(file));
    }

    private static byte[] binaryStl(float[]... triangles) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LittleEndianDataOutput out = new LittleEndianDataOutput(baos)) {
            byte[] header = new byte[80];
            out.write(header);
            out.writeInt(triangles.length);

            for (float[] triangle : triangles) {
                for (float value : triangle) {
                    out.writeFloat(value);
                }
                out.writeShort(0); // attribute byte count
            }
        }
        return baos.toByteArray();
    }

    private static float[] triangle(
            float nx, float ny, float nz,
            float ax, float ay, float az,
            float bx, float by, float bz,
            float cx, float cy, float cz
    ) {
        return new float[] {
                nx, ny, nz,
                ax, ay, az,
                bx, by, bz,
                cx, cy, cz
        };
    }

    private static final class LittleEndianDataOutput implements AutoCloseable {
        private final ByteArrayOutputStream out;

        private LittleEndianDataOutput(ByteArrayOutputStream out) {
            this.out = out;
        }

        public void write(byte[] bytes) throws IOException {
            out.write(bytes);
        }

        public void writeInt(int v) throws IOException {
            out.write(v & 0xFF);
            out.write((v >>> 8) & 0xFF);
            out.write((v >>> 16) & 0xFF);
            out.write((v >>> 24) & 0xFF);
        }

        public void writeShort(int v) throws IOException {
            out.write(v & 0xFF);
            out.write((v >>> 8) & 0xFF);
        }

        public void writeFloat(float v) throws IOException {
            writeInt(Float.floatToIntBits(v));
        }

        @Override
        public void close() {
            // nothing to close; ByteArrayOutputStream does not need it
        }
    }
}