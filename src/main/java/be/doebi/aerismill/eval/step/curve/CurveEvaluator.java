package be.doebi.aerismill.eval.step.curve;


import be.doebi.aerismill.model.geom.curve.CircleCurve3;
import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Circle;
import be.doebi.aerismill.model.step.geometry.Line;

public interface CurveEvaluator {
    LineCurve3 evaluateLine(Line line);
    CircleCurve3 evaluateCircle(Circle circle);
    Curve3 evaluate(StepEntity entity);
}