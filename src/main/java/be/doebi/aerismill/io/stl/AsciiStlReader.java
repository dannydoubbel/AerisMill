package be.doebi.aerismill.io.stl;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class AsciiStlReader {

    public Mesh read(Path path) throws IOException {
        Objects.requireNonNull(path, "path must not be null");

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return read(reader);
        }
    }

    Mesh read(BufferedReader reader) throws IOException {
        List<RawTriangle> rawTriangles = new ArrayList<>();

        boolean inFacet = false;
        boolean inLoop = false;
        List<Point3> currentVertices = new ArrayList<>(3);

        String line;
        int lineNumber = 0;

        while ((line = reader.readLine()) != null) {
            lineNumber++;
            String trimmed = line.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            String[] tokens = trimmed.split("\\s+");

            if (isSolid(tokens) || isEndSolid(tokens)) {
                continue;
            }

            if (isFacet(tokens)) {
                if (inFacet || inLoop) {
                    throw parseError(lineNumber, "Unexpected FACET start");
                }
                inFacet = true;
                currentVertices.clear();
                continue;
            }

            if (isOuterLoop(tokens)) {
                if (!inFacet || inLoop) {
                    throw parseError(lineNumber, "Unexpected OUTER LOOP");
                }
                inLoop = true;
                continue;
            }

            if (isVertex(tokens)) {
                if (!inLoop) {
                    throw parseError(lineNumber, "VERTEX outside OUTER LOOP");
                }
                if (currentVertices.size() >= 3) {
                    throw parseError(lineNumber, "Too many VERTEX lines in facet");
                }

                currentVertices.add(parseVertex(tokens, lineNumber));
                continue;
            }

            if (isEndLoop(tokens)) {
                if (!inLoop) {
                    throw parseError(lineNumber, "Unexpected ENDLOOP");
                }
                if (currentVertices.size() != 3) {
                    throw parseError(lineNumber, "Facet must contain exactly 3 vertices before ENDLOOP");
                }
                inLoop = false;
                continue;
            }

            if (isEndFacet(tokens)) {
                if (!inFacet || inLoop) {
                    throw parseError(lineNumber, "Unexpected ENDFACET");
                }
                if (currentVertices.size() != 3) {
                    throw parseError(lineNumber, "Facet must contain exactly 3 vertices");
                }

                Point3 a = currentVertices.get(0);
                Point3 b = currentVertices.get(1);
                Point3 c = currentVertices.get(2);

                if (a.equals(b) || b.equals(c) || a.equals(c)) {
                    throw parseError(lineNumber, "Facet contains duplicate vertex positions");
                }

                rawTriangles.add(new RawTriangle(a, b, c));
                currentVertices = new ArrayList<>(3);
                inFacet = false;
                continue;
            }

            throw parseError(lineNumber, "Unexpected line: " + trimmed);
        }

        if (inFacet || inLoop) {
            throw new IllegalArgumentException("Unexpected end of ASCII STL while reading facet");
        }

        return buildMesh(rawTriangles);
    }

    private static Mesh buildMesh(List<RawTriangle> rawTriangles) {
        List<MeshVertex> vertices = new ArrayList<>();
        List<MeshTriangle> triangles = new ArrayList<>();
        Map<Point3, Integer> indexByPoint = new LinkedHashMap<>();

        for (RawTriangle raw : rawTriangles) {
            int a = vertexIndex(raw.a(), indexByPoint, vertices);
            int b = vertexIndex(raw.b(), indexByPoint, vertices);
            int c = vertexIndex(raw.c(), indexByPoint, vertices);

            triangles.add(new MeshTriangle(a, b, c));
        }

        return new Mesh(vertices, triangles);
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

    private static Point3 parseVertex(String[] tokens, int lineNumber) {
        if (tokens.length != 4) {
            throw parseError(lineNumber, "VERTEX line must contain exactly 3 coordinates");
        }

        try {
            double x = Double.parseDouble(tokens[1]);
            double y = Double.parseDouble(tokens[2]);
            double z = Double.parseDouble(tokens[3]);

            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
                throw parseError(lineNumber, "VERTEX contains non-finite coordinate");
            }

            return new Point3(x, y, z);
        } catch (NumberFormatException ex) {
            throw parseError(lineNumber, "Invalid numeric value in VERTEX line");
        }
    }

    private static boolean isSolid(String[] tokens) {
        return tokens.length >= 1 && tokens[0].equalsIgnoreCase("solid");
    }

    private static boolean isEndSolid(String[] tokens) {
        return tokens.length >= 1 && tokens[0].equalsIgnoreCase("endsolid");
    }

    private static boolean isFacet(String[] tokens) {
        return tokens.length >= 2
                && tokens[0].equalsIgnoreCase("facet")
                && tokens[1].equalsIgnoreCase("normal");
    }

    private static boolean isOuterLoop(String[] tokens) {
        return tokens.length >= 2
                && tokens[0].equalsIgnoreCase("outer")
                && tokens[1].equalsIgnoreCase("loop");
    }

    private static boolean isVertex(String[] tokens) {
        return tokens.length >= 1 && tokens[0].equalsIgnoreCase("vertex");
    }

    private static boolean isEndLoop(String[] tokens) {
        return tokens.length >= 1 && tokens[0].equalsIgnoreCase("endloop");
    }

    private static boolean isEndFacet(String[] tokens) {
        return tokens.length >= 1 && tokens[0].equalsIgnoreCase("endfacet");
    }

    private static IllegalArgumentException parseError(int lineNumber, String message) {
        return new IllegalArgumentException("ASCII STL parse error at line " + lineNumber + ": " + message);
    }

    private record RawTriangle(Point3 a, Point3 b, Point3 c) {
    }
}