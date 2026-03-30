package be.doebi.aerismill.eval.step.context;

import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.surface.Surface3;

import java.util.HashMap;
import java.util.Map;

public final class StepEvaluationCache {
    private final Map<String, Frame3> frameCache = new HashMap<>();
    private final Map<String, Curve3> curveCache = new HashMap<>();
    private final Map<String, Surface3> surfaceCache = new HashMap<>();
    private final Map<String, Object> topologyCache = new HashMap<>();
}