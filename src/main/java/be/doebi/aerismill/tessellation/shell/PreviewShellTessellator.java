package be.doebi.aerismill.tessellation.shell;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.ShellGeom;
import be.doebi.aerismill.model.mesh.Mesh;
import be.doebi.aerismill.model.mesh.MeshTriangle;
import be.doebi.aerismill.model.mesh.MeshVertex;
import be.doebi.aerismill.tessellation.face.FaceMeshPatch;
import be.doebi.aerismill.tessellation.face.FaceTessellator;
import be.doebi.aerismill.ui.AppConsole;

import java.util.ArrayList;
import java.util.List;

public class PreviewShellTessellator implements ShellTessellator {

    private final FaceTessellator faceTessellator;

    public PreviewShellTessellator(FaceTessellator faceTessellator) {
        if (faceTessellator == null) {
            throw new IllegalArgumentException("Face tessellator must not be null.");
        }
        this.faceTessellator = faceTessellator;
    }

    @Override
    public Mesh tessellate(ShellGeom shell) {
        if (shell == null) {
            throw new IllegalArgumentException("Shell must not be null.");
        }
        if (shell.faces() == null) {
            throw new IllegalArgumentException("Shell faces must not be null.");
        }

        List<MeshVertex> combinedVertices = new ArrayList<>();
        List<MeshTriangle> combinedTriangles = new ArrayList<>();
        List<String> skippedReasons = new ArrayList<>();

        for (FaceGeom face : shell.faces()) {
            if (face == null) {
                throw new IllegalArgumentException("Shell faces must not contain null faces.");
            }

            try {
                FaceMeshPatch patch = faceTessellator.tessellate(face);
                validateFaceMeshPatch(patch);

                int vertexOffset = combinedVertices.size();

                appendVertices(combinedVertices, patch.vertices());
                appendTrianglesWithOffset(combinedTriangles, patch.triangles(), vertexOffset);

            } catch (IllegalArgumentException ex) {
                String surfaceType = face.surface() == null
                        ? "null"
                        : face.surface().getClass().getSimpleName();

                String reason = "Face " + face.stepId()
                        + " [" + surfaceType + "]: " + ex.getMessage();

                skippedReasons.add(reason);
                AppConsole.log(reason);
            }
        }

        if (combinedVertices.isEmpty() || combinedTriangles.isEmpty()) {
            String firstReason = skippedReasons.isEmpty()
                    ? "No face produced previewable mesh."
                    : skippedReasons.getFirst();

            throw new IllegalArgumentException(
                    "No previewable faces found in shell. First reason: " + firstReason
            );
        }

        return new Mesh(combinedVertices, combinedTriangles);
    }

    void validateFaceMeshPatch(FaceMeshPatch patch) {
        if (patch == null) {
            throw new IllegalArgumentException("Face tessellator must not return null patch.");
        }
        if (patch.vertices() == null) {
            throw new IllegalArgumentException("Face mesh patch vertices must not be null.");
        }
        if (patch.triangles() == null) {
            throw new IllegalArgumentException("Face mesh patch triangles must not be null.");
        }
    }

    void appendVertices(List<MeshVertex> targetVertices, List<Point3> sourcePoints) {
        if (targetVertices == null) {
            throw new IllegalArgumentException("Target vertices must not be null.");
        }
        if (sourcePoints == null) {
            throw new IllegalArgumentException("Source points must not be null.");
        }

        for (Point3 point : sourcePoints) {
            if (point == null) {
                throw new IllegalArgumentException("Face mesh patch vertices must not contain null points.");
            }

            int nextIndex = targetVertices.size();
            targetVertices.add(new MeshVertex(nextIndex, point));
        }
    }

    void appendTrianglesWithOffset(List<MeshTriangle> targetTriangles, List<int[]> sourceTriangles, int vertexOffset) {
        if (targetTriangles == null) {
            throw new IllegalArgumentException("Target triangles must not be null.");
        }
        if (sourceTriangles == null) {
            throw new IllegalArgumentException("Source triangles must not be null.");
        }

        for (int[] triangle : sourceTriangles) {
            targetTriangles.add(copyTriangleWithOffset(triangle, vertexOffset));
        }
    }

    MeshTriangle copyTriangleWithOffset(int[] triangle, int vertexOffset) {
        if (triangle == null || triangle.length != 3) {
            throw new IllegalArgumentException("Each triangle must contain exactly three vertex indices.");
        }

        return new MeshTriangle(
                triangle[0] + vertexOffset,
                triangle[1] + vertexOffset,
                triangle[2] + vertexOffset
        );
    }
}