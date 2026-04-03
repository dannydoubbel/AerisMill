package be.doebi.aerismill.eval.step.surface;

import be.doebi.aerismill.model.geom.surface.*;
import be.doebi.aerismill.model.step.geometry.ConicalSurface;
import be.doebi.aerismill.model.step.geometry.CylindricalSurface;
import be.doebi.aerismill.model.step.geometry.Plane;
import be.doebi.aerismill.model.step.geometry.BSplineSurfaceWithKnots;
import be.doebi.aerismill.model.step.geometry.SphericalSurface;
import be.doebi.aerismill.model.step.geometry.ToroidalSurface;
import be.doebi.aerismill.model.step.geometry.SurfaceOfRevolution;


public interface SurfaceEvaluator {
    Surface3 evaluate(Plane plane);
    Surface3 evaluate(CylindricalSurface cylindricalSurface);
    Surface3 evaluate(ConicalSurface conicalSurface);
    Surface3 evaluate(BSplineSurfaceWithKnots bSplineSurfaceWithKnots);
    Surface3 evaluate(SphericalSurface sphericalSurface);
    Surface3 evaluate(ToroidalSurface toroidalSurface);
    Surface3 evaluate(SurfaceOfRevolution surfaceOfRevolution);

    SurfaceOfRevolution3 evaluateSurfaceOfRevolution(SurfaceOfRevolution surfaceOfRevolution);
    ToroidalSurface3 evaluateToroidalSurface(ToroidalSurface toroidalSurface);
    SphericalSurface3 evaluateSphericalSurface(SphericalSurface sphericalSurface);
    BSplineSurface3 evaluateBSplineSurfaceWithKnots(BSplineSurfaceWithKnots bSplineSurfaceWithKnots);
    PlaneSurface3 evaluatePlane(Plane plane);
    CylindricalSurface3 evaluateCylindricalSurface(CylindricalSurface cylindricalSurface);
    ConicalSurface3 evaluateConicalSurface(ConicalSurface conicalSurface);
}