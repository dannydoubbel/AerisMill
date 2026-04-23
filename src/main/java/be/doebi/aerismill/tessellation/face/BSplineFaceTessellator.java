package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.surface.BSplineSurface3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;

import java.util.ArrayList;
import java.util.List;

public final class BSplineFaceTessellator implements FaceTessellator {

    private static final int DEFAULT_U_STEPS = 16;
    private static final int DEFAULT_V_STEPS = 16;

    @Override
    public FaceMeshPatch tessellate(FaceGeom face) {
        if (face == null) {
            throw new IllegalArgumentException("Face must not be null.");
        }
        if (!(face.surface() instanceof BSplineSurface3 surface)) {
            throw new IllegalArgumentException(faceLabel(face) + ": only BSpline faces are supported here.");
        }

        double startU = surface.startU();
        double endU = surface.endU();
        double startV = surface.startV();
        double endV = surface.endV();

        if (!(endU > startU)) {
            throw new IllegalArgumentException(faceLabel(face) + ": invalid BSpline U domain.");
        }
        if (!(endV > startV)) {
            throw new IllegalArgumentException(faceLabel(face) + ": invalid BSpline V domain.");
        }

        int uSteps = DEFAULT_U_STEPS;
        int vSteps = DEFAULT_V_STEPS;

        List<Point3> vertices = new ArrayList<>((uSteps + 1) * (vSteps + 1));
        List<int[]> triangles = new ArrayList<>(uSteps * vSteps * 2);

        for (int iv = 0; iv <= vSteps; iv++) {
            double tv = iv / (double) vSteps;
            double v = startV + (endV - startV) * tv;

            for (int iu = 0; iu <= uSteps; iu++) {
                double tu = iu / (double) uSteps;
                double u = startU + (endU - startU) * tu;
                vertices.add(surface.pointAt(u, v));
            }
        }

        int rowSize = uSteps + 1;

        for (int iv = 0; iv < vSteps; iv++) {
            for (int iu = 0; iu < uSteps; iu++) {
                int a = iv * rowSize + iu;
                int b = iv * rowSize + (iu + 1);
                int c = (iv + 1) * rowSize + iu;
                int d = (iv + 1) * rowSize + (iu + 1);

                if (face.sameSense()) {
                    triangles.add(new int[]{a, b, d});
                    triangles.add(new int[]{a, d, c});
                } else {
                    triangles.add(new int[]{a, d, b});
                    triangles.add(new int[]{a, c, d});
                }
            }
        }

        if (triangles.isEmpty()) {
            throw new IllegalArgumentException(faceLabel(face) + ": BSpline tessellation produced no triangles.");
        }

        return new FaceMeshPatch(vertices, triangles, SurfaceFamily.BSPLINE);
    }

    private String faceLabel(FaceGeom face) {
        if (face == null || face.stepId() == null || face.stepId().isBlank()) {
            return "Face <unknown>";
        }
        return "Face " + face.stepId();
    }
}