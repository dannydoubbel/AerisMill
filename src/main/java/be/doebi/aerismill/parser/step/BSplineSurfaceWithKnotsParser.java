package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.geometry.BSplineSurfaceWithKnots;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BSplineSurfaceWithKnotsParser implements EntityParser<BSplineSurfaceWithKnots> {
    @Override
    public BSplineSurfaceWithKnots parse(
            StepEntity entity,
            List<String> params,
            Map<String, Object> parsedEntities
    ) {
        int uDegree = Integer.parseInt(params.get(0).trim());
        int vDegree = Integer.parseInt(params.get(1).trim());

        List<List<StepEntity>> controlPointsList = parseControlPointGrid(params.get(2), parsedEntities);

        String surfaceForm = params.get(3).trim();
        boolean uClosed = StepParserUtils.parseStepBoolean(params.get(4));
        boolean vClosed = StepParserUtils.parseStepBoolean(params.get(5));
        boolean selfIntersect = StepParserUtils.parseStepBoolean(params.get(6));

        List<Integer> uMultiplicities = StepParserUtils.parseIntegerList(params.get(7));
        List<Integer> vMultiplicities = StepParserUtils.parseIntegerList(params.get(8));
        List<Double> uKnots = StepParserUtils.parseDoubleList(params.get(9));
        List<Double> vKnots = StepParserUtils.parseDoubleList(params.get(10));

        String knotSpec = params.get(11).trim();

        return new BSplineSurfaceWithKnots(
                entity.getId(),
                entity.getRawParameters(),
                uDegree,
                vDegree,
                controlPointsList,
                surfaceForm,
                uClosed,
                vClosed,
                selfIntersect,
                uMultiplicities,
                vMultiplicities,
                uKnots,
                vKnots,
                knotSpec
        );
    }

    private List<List<StepEntity>> parseControlPointGrid(String value, Map<String, Object> parsedEntities) {
        String inside = StepParserUtils.stripOuterParens(value);

        List<String> rowStrings = StepParserUtils.splitTopLevelGroups(inside);
        List<List<StepEntity>> grid = new ArrayList<>();

        for (String rowString : rowStrings) {
            String rowInside = StepParserUtils.stripOuterParens(rowString);
            String[] ids = rowInside.split(",");

            List<StepEntity> row = new ArrayList<>();
            for (String id : ids) {
                Object resolved = parsedEntities.get(id.trim());
                if (!(resolved instanceof StepEntity stepEntity)) {
                    throw new IllegalArgumentException("Could not resolve control point: " + id.trim());
                }
                row.add(stepEntity);
            }

            grid.add(row);
        }

        return grid;
    }

}
