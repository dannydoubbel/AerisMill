package be.doebi.aerismill.eval.step.surface;

import be.doebi.aerismill.model.geom.surface.Surface3;
import be.doebi.aerismill.model.step.base.StepEntity;

public interface SurfaceEvaluator {
    Surface3 evaluateSurface(StepEntity entity);
}