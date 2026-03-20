package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.StepEntity;
import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BSplineCurveWithKnotsParser implements EntityParser<BSplineCurveWithKnots>{
    @Override
    public BSplineCurveWithKnots parse(
            StepEntity entity,
            List<String> params,
            Map<String, Object> parsedEntities
    ) {
        int degree = Integer.parseInt(params.get(0).trim());

        List<StepEntity> controlPoints = parseControlPoints(params.get(1), parsedEntities);

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
                controlPoints,
                curveForm,
                closedCurve,
                selfIntersect,
                knotMultiplicities,
                knots,
                knotSpec
        );
    }

    private List<StepEntity> parseControlPoints(String value, Map<String, Object> parsedEntities) {
        String inside = StepParserUtils.stripOuterParens(value);
        String[] ids = inside.split(",");

        List<StepEntity> result = new ArrayList<>();
        for (String id : ids) {
            Object resolved = parsedEntities.get(id.trim());
            if (!(resolved instanceof StepEntity stepEntity)) {
                throw new IllegalArgumentException("Could not resolve control point: " + id.trim());
            }
            result.add(stepEntity);
        }

        return result;
    }
}
