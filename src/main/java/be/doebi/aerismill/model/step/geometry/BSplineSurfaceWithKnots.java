package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

import java.util.List;

public class BSplineSurfaceWithKnots extends GeometricEntity {
    private final int uDegree;
    private final int vDegree;
    private final List<List<StepEntity>> controlPointsList;
    private final String surfaceForm;
    private final boolean uClosed;
    private final boolean vClosed;
    private final boolean selfIntersect;
    private final List<Integer> uMultiplicities;
    private final List<Integer> vMultiplicities;
    private final List<Double> uKnots;
    private final List<Double> vKnots;
    private final String knotSpec;

    public BSplineSurfaceWithKnots(
            String id,
            String rawParameters,
            int uDegree,
            int vDegree,
            List<List<StepEntity>> controlPointsList,
            String surfaceForm,
            boolean uClosed,
            boolean vClosed,
            boolean selfIntersect,
            List<Integer> uMultiplicities,
            List<Integer> vMultiplicities,
            List<Double> uKnots,
            List<Double> vKnots,
            String knotSpec
    ) {
        super(id, StepEntityType.B_SPLINE_SURFACE_WITH_KNOTS.getName(), rawParameters);
        this.uDegree = uDegree;
        this.vDegree = vDegree;
        this.controlPointsList = controlPointsList;
        this.surfaceForm = surfaceForm;
        this.uClosed = uClosed;
        this.vClosed = vClosed;
        this.selfIntersect = selfIntersect;
        this.uMultiplicities = uMultiplicities;
        this.vMultiplicities = vMultiplicities;
        this.uKnots = uKnots;
        this.vKnots = vKnots;
        this.knotSpec = knotSpec;
    }

    public int getUDegree() {
        return uDegree;
    }

    public int getVDegree() {
        return vDegree;
    }

    public List<List<StepEntity>> getControlPointsList() {
        return controlPointsList;
    }

    public String getSurfaceForm() {
        return surfaceForm;
    }

    public boolean isUClosed() {
        return uClosed;
    }

    public boolean isVClosed() {
        return vClosed;
    }

    public boolean isSelfIntersect() {
        return selfIntersect;
    }

    public List<Integer> getUMultiplicities() {
        return uMultiplicities;
    }

    public List<Integer> getVMultiplicities() {
        return vMultiplicities;
    }

    public List<Double> getUKnots() {
        return uKnots;
    }

    public List<Double> getVKnots() {
        return vKnots;
    }

    public String getKnotSpec() {
        return knotSpec;
    }
}
