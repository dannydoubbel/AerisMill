package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.PolygonTriangulator;
import be.doebi.aerismill.tessellation.projection.PlaneProjector;

public class PlanarFaceTessellator implements FaceTessellator {

    private final EdgeDiscretizer edgeDiscretizer;
    private final PolygonTriangulator polygonTriangulator;
    private final PlaneProjector planeProjector;
    private final GeometryTolerance tolerance;

    public PlanarFaceTessellator(
            EdgeDiscretizer edgeDiscretizer,
            PolygonTriangulator polygonTriangulator,
            PlaneProjector planeProjector,
            GeometryTolerance tolerance
    ) {
        this.edgeDiscretizer = edgeDiscretizer;
        this.polygonTriangulator = polygonTriangulator;
        this.planeProjector = planeProjector;
        this.tolerance = tolerance;
    }

    @Override
    public FaceMeshPatch tessellate(FaceGeom face) {
        if (!(face.surface() instanceof PlaneSurface3)) {
            throw new IllegalArgumentException("Only planar faces are supported for now.");
        }
        if (face.bounds() == null || face.bounds().isEmpty()) {
            throw new IllegalArgumentException("Face must have at least one bound.");
        }
        if (face.bounds().size() > 1) {
            throw new IllegalArgumentException("Only single-bound planar faces are supported for now.");
        }

        LoopGeom outerBound = face.bounds().getFirst();

        if (outerBound.edges() == null || outerBound.edges().isEmpty()) {
            throw new IllegalArgumentException("Face bound must contain at least one edge.");
        }

        var firstNonNullEdge = outerBound.edges().stream()
                .filter(java.util.Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Face bound must contain at least one non-null edge."));

        edgeDiscretizer.discretize(firstNonNullEdge, tolerance);

        return new FaceMeshPatch(java.util.List.of(), java.util.List.of());
    }


}