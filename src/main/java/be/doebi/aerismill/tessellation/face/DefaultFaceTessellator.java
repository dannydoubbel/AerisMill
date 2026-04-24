package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.surface.*;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.PolygonTriangulator;
import be.doebi.aerismill.tessellation.projection.PlaneProjector;

public final class DefaultFaceTessellator implements FaceTessellator {

    private final PlanarFaceTessellator planarFaceTessellator;
    private final CylindricalFaceTessellator cylindricalFaceTessellator;
    private final ConicalFaceTessellator conicalFaceTessellator;
    private final ToroidalFaceTessellator toroidalFaceTessellator;
    private final BSplineFaceTessellator bSplineFaceTessellator;

    public DefaultFaceTessellator(
            EdgeDiscretizer edgeDiscretizer,
            PolygonTriangulator polygonTriangulator,
            PlaneProjector planeProjector,
            GeometryTolerance tolerance
    ) {
        this.planarFaceTessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                tolerance
        );
        this.cylindricalFaceTessellator = new CylindricalFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                tolerance
        );
        this.conicalFaceTessellator = new ConicalFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                tolerance
        );
        this.toroidalFaceTessellator = new ToroidalFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                tolerance
        );
        this.bSplineFaceTessellator = new BSplineFaceTessellator();

    }

    @Override
    public FaceMeshPatch tessellate(FaceGeom face) {
        if (face.surface() instanceof PlaneSurface3) {
            return planarFaceTessellator.tessellate(face);
        }
        if (face.surface() instanceof CylindricalSurface3) {
            return cylindricalFaceTessellator.tessellate(face);
        }
        if (face.surface() instanceof ConicalSurface3) {
            return conicalFaceTessellator.tessellate(face);
        }

        if (face.surface() instanceof ToroidalSurface3) {
            return toroidalFaceTessellator.tessellate(face);
        }

        if (face.surface() instanceof BSplineSurface3) {
            return bSplineFaceTessellator.tessellate(face);
        }

        String surfaceName = face == null || face.surface() == null
                ? "<unknown>"
                : face.surface().getClass().getSimpleName();

        throw new IllegalArgumentException(
                (face == null || face.stepId() == null ? "Face <unknown>" : "Face " + face.stepId())
                        + " [" + surfaceName + "]"
                        + ": only planar, cylindrical, conical, toroidal and bSpline faces are supported for now."
        );
    }
}