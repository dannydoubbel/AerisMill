package be.doebi.aerismill.eval.step.curve;

import be.doebi.aerismill.model.geom.curve.CircleCurve3;
import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.curve.EllipseCurve3;
import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.step.geometry.Circle;
import be.doebi.aerismill.model.step.geometry.Ellipse;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.geom.curve.BSplineCurve3;
import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;

public interface CurveEvaluator {
    Curve3 evaluate(Line line);
    Curve3 evaluate(Circle circle);
    Curve3 evaluate(Ellipse ellipse);
    Curve3 evaluate(BSplineCurveWithKnots bSplineCurveWithKnots);

    BSplineCurve3 evaluateBSplineCurveWithKnots(BSplineCurveWithKnots bSplineCurveWithKnots);

    LineCurve3 evaluateLine(Line line);
    CircleCurve3 evaluateCircle(Circle circle);
    EllipseCurve3 evaluateEllipse(Ellipse ellipse);
}