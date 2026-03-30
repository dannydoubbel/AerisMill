package be.doebi.aerismill.eval.step.curve;

import be.doebi.aerismill.model.geom.curve.CircleCurve3;
import be.doebi.aerismill.model.geom.curve.Curve3;
import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.Circle;
import be.doebi.aerismill.model.step.geometry.Line;

public final class DefaultCurveEvaluator implements CurveEvaluator {

    @Override
    public Curve3 evaluate(StepEntity entity) {
        return switch (entity.getType()) {
            case LINE -> evaluateLine((Line) entity);
            case CIRCLE -> evaluateCircle((Circle) entity);
            default -> throw new UnsupportedOperationException(
                    "Unsupported curve type: " + entity.getType()
            );
        };
    }

    @Override
    public LineCurve3 evaluateLine(Line line) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public CircleCurve3 evaluateCircle(Circle circle) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}