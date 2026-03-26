package be.doebi.aerismill.model.step.geometry;

import be.doebi.aerismill.model.step.GeometricEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;

import java.util.List;

public class BSplineCurveWithKnots extends GeometricEntity {

    private final int degree;
    private final List<StepEntity> controlPoints;
    private final String curveForm;
    private final boolean closedCurve;
    private final boolean selfIntersect;
    private final List<Integer> knotMultiplicities;
    private final List<Double> knots;
    private final String knotSpec;

    public BSplineCurveWithKnots(
            String id,
            String rawParameters,
            int degree,
            List<StepEntity> controlPoints,
            String curveForm,
            boolean closedCurve,
            boolean selfIntersect,
            List<Integer> knotMultiplicities,
            List<Double> knots,
            String knotSpec
    ) {
        super(id, StepEntityType.B_SPLINE_CURVE_WITH_KNOTS.getName(), rawParameters);

        this.degree = degree;
        this.controlPoints = controlPoints;
        this.curveForm = curveForm;
        this.closedCurve = closedCurve;
        this.selfIntersect = selfIntersect;
        this.knotMultiplicities = knotMultiplicities;
        this.knots = knots;
        this.knotSpec = knotSpec;
    }

    public int getDegree() {
        return degree;
    }

    public List<StepEntity> getControlPoints() {
        return controlPoints;
    }

    public String getCurveForm() {
        return curveForm;
    }

    public boolean isClosedCurve() {
        return closedCurve;
    }

    public boolean isSelfIntersect() {
        return selfIntersect;
    }

    public List<Integer> getKnotMultiplicities() {
        return knotMultiplicities;
    }

    public List<Double> getKnots() {
        return knots;
    }

    public String getKnotSpec() {
        return knotSpec;
    }
}