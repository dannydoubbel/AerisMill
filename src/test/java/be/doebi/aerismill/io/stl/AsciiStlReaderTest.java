package be.doebi.aerismill.io.stl;

import be.doebi.aerismill.model.mesh.Mesh;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsciiStlReaderTest {

    @TempDir
    Path tempDir;

    @Test
    void read_singleTriangle_returnsMesh() throws IOException {
        Path file = tempDir.resolve("single_triangle_ascii.stl");
        Files.writeString(file, """
                solid single
                  facet normal 0 0 1
                    outer loop
                      vertex 0 0 0
                      vertex 1 0 0
                      vertex 0 1 0
                    endloop
                  endfacet
                endsolid single
                """);

        AsciiStlReader reader = new AsciiStlReader();
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
        Path file = tempDir.resolve("square_ascii.stl");
        Files.writeString(file, """
                solid square
                  facet normal 0 0 1
                    outer loop
                      vertex 0 0 0
                      vertex 1 0 0
                      vertex 1 1 0
                    endloop
                  endfacet
                  facet normal 0 0 1
                    outer loop
                      vertex 0 0 0
                      vertex 1 1 0
                      vertex 0 1 0
                    endloop
                  endfacet
                endsolid square
                """);

        AsciiStlReader reader = new AsciiStlReader();
        Mesh mesh = reader.read(file);

        assertEquals(4, mesh.vertexCount());
        assertEquals(2, mesh.triangleCount());

        assertEquals(0.0, mesh.bounds().minX());
        assertEquals(0.0, mesh.bounds().minY());
        assertEquals(0.0, mesh.bounds().minZ());

        assertEquals(1.0, mesh.bounds().maxX());
        assertEquals(1.0, mesh.bounds().maxY());
        assertEquals(0.0, mesh.bounds().maxZ());
    }
}