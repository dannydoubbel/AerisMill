package be.doebi.aerismill.eval.step.surface;

import be.doebi.aerismill.model.geom.surface.CylindricalSurface3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.surface.Surface3;
import be.doebi.aerismill.model.step.geometry.CylindricalSurface;
import be.doebi.aerismill.model.step.geometry.Plane;

public interface SurfaceEvaluator {
    Surface3 evaluate(Plane plane);
    Surface3 evaluate(CylindricalSurface cylindricalSurface);

    PlaneSurface3 evaluatePlane(Plane plane);
    CylindricalSurface3 evaluateCylindricalSurface(CylindricalSurface cylindricalSurface);
}