package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
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

        return new FaceMeshPatch(java.util.List.of(), java.util.List.of());
    }
}