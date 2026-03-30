package be.doebi.aerismill.eval.step.context;

import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.Surface3;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class StepEvaluationCache {
    private final Map<String, Point3> pointCache = new HashMap<>();
    private final Map<String, UnitVec3> directionCache = new HashMap<>();
    private final Map<String, Vec3> vectorCache = new HashMap<>();
    private final Map<String, Frame3> frameCache = new HashMap<>();
    private final Map<String, Curve3> curveCache = new HashMap<>();
    private final Map<String, Surface3> surfaceCache = new HashMap<>();
    private final Map<String, Object> topologyCache = new HashMap<>();

    public Point3 getPoint(String stepId) {
        return pointCache.get(stepId);
    }

    public void putPoint(String stepId, Point3 point) {
        pointCache.put(requireStepId(stepId), Objects.requireNonNull(point, "point must not be null"));
    }

    public UnitVec3 getDirection(String stepId) {
        return directionCache.get(stepId);
    }

    public void putDirection(String stepId, UnitVec3 direction) {
        directionCache.put(requireStepId(stepId), Objects.requireNonNull(direction, "direction must not be null"));
    }

    public Vec3 getVector(String stepId) {
        return vectorCache.get(stepId);
    }

    public void putVector(String stepId, Vec3 vector) {
        vectorCache.put(requireStepId(stepId), Objects.requireNonNull(vector, "vector must not be null"));
    }

    public Frame3 getFrame(String stepId) {
        return frameCache.get(stepId);
    }

    public void putFrame(String stepId, Frame3 frame) {
        frameCache.put(requireStepId(stepId), Objects.requireNonNull(frame, "frame must not be null"));
    }

    public Curve3 getCurve(String stepId) {
        return curveCache.get(stepId);
    }

    public void putCurve(String stepId, Curve3 curve) {
        curveCache.put(requireStepId(stepId), Objects.requireNonNull(curve, "curve must not be null"));
    }

    public Surface3 getSurface(String stepId) {
        return surfaceCache.get(stepId);
    }

    public void putSurface(String stepId, Surface3 surface) {
        surfaceCache.put(requireStepId(stepId), Objects.requireNonNull(surface, "surface must not be null"));
    }

    public Object getTopology(String stepId) {
        return topologyCache.get(stepId);
    }

    public void putTopology(String stepId, Object topology) {
        topologyCache.put(requireStepId(stepId), Objects.requireNonNull(topology, "topology must not be null"));
    }

    public void clear() {
        pointCache.clear();
        directionCache.clear();
        vectorCache.clear();
        frameCache.clear();
        curveCache.clear();
        surfaceCache.clear();
        topologyCache.clear();
    }

    private static String requireStepId(String stepId) {
        return Objects.requireNonNull(stepId, "stepId must not be null");
    }
}