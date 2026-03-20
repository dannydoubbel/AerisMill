package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;

import java.util.List;
import java.util.Map;

public class BSplineCurveWithKnotsParser implements EntityParser<BSplineCurveWithKnots>{
    @Override
    public BSplineCurveWithKnots parse(
            StepEntity entity,
            List<String> params,
            Map<String, Object> parsedEntities
    ) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
