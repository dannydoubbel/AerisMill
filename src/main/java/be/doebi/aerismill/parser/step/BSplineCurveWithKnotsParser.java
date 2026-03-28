package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;

import java.util.List;
import java.util.Map;

public class BSplineCurveWithKnotsParser implements EntityParser<BSplineCurveWithKnots> {

    @Override
    public BSplineCurveWithKnots parse(
            StepEntity entity,
            List<String> params,
            Map<String, Object> parsedEntities
    ) {
        int degree = Integer.parseInt(params.get(0).trim());

        List<String> controlPointRefs = StepParserUtils.parseReferenceList(params.get(1));

        String curveForm = params.get(2).trim();
        boolean closedCurve = StepParserUtils.parseStepBoolean(params.get(3));
        boolean selfIntersect = StepParserUtils.parseStepBoolean(params.get(4));

        List<Integer> knotMultiplicities = StepParserUtils.parseIntegerList(params.get(5));
        List<Double> knots = StepParserUtils.parseDoubleList(params.get(6));
        String knotSpec = params.get(7).trim();

        return new BSplineCurveWithKnots(
                entity.getId(),
                entity.getRawParameters(),
                degree,
                controlPointRefs,
                curveForm,
                closedCurve,
                selfIntersect,
                knotMultiplicities,
                knots,
                knotSpec
        );
    }

    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.B_SPLINE_CURVE_WITH_KNOTS, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);
        return parse(entity, params, Map.of());
    }
}