package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepEntityType;
import be.doebi.aerismill.model.step.base.StepLogical;
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
        if (params.size() < 13) {
            throw new IllegalArgumentException(
                    "Expected 13 params for " + entity.getType() +
                            " " + entity.getId() +
                            " but got " + params.size() +
                            " raw=" + entity.getRawParameters()
            );
        }

        String name = StepParserUtils.parseStepString(params.get(0));
        int uDegree = StepParserUtils.parseRequiredInt(params.get(1), "uDegree", entity);
        int vDegree = StepParserUtils.parseRequiredInt(params.get(2), "vDegree", entity);

        List<List<String>> controlPointRefs = parseControlPointRefGrid(params.get(3));

        String surfaceForm = params.get(4).trim();
        StepLogical uClosed = StepParserUtils.parseStepLogical(params.get(5));
        StepLogical vClosed = StepParserUtils.parseStepLogical(params.get(6));
        StepLogical selfIntersect = StepParserUtils.parseStepLogical(params.get(7));

        List<Integer> uMultiplicities = StepParserUtils.parseIntegerList(params.get(8));
        List<Integer> vMultiplicities = StepParserUtils.parseIntegerList(params.get(9));
        List<Double> uKnots = StepParserUtils.parseDoubleList(params.get(10));
        List<Double> vKnots = StepParserUtils.parseDoubleList(params.get(11));

        String knotSpec = params.get(12).trim();

        return new BSplineSurfaceWithKnots(
                entity.getId(),
                entity.getRawParameters(),
                name,
                uDegree,
                vDegree,
                controlPointRefs,
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















    @Override
    public StepEntity parse(String id, String rawParameters) {
        StepEntity entity = new StepEntity(id, StepEntityType.B_SPLINE_SURFACE_WITH_KNOTS, rawParameters);
        List<String> params = StepParserUtils.splitTopLevelParameters(rawParameters);

        String uDegreeToken = params.get(0).trim();
        String vDegreeToken = params.get(1).trim();

        System.out.println("BSPLINE " + entity.getId() + " uDegreeToken=[" + uDegreeToken + "] vDegreeToken=[" + vDegreeToken + "]");


        return parse(entity, params, Map.of());
    }

    private List<List<String>> parseControlPointRefGrid(String value) {
        String inside = StepParserUtils.stripOuterParens(value);

        List<String> rowStrings = StepParserUtils.splitTopLevelGroups(inside);
        List<List<String>> grid = new ArrayList<>();

        for (String rowString : rowStrings) {
            String rowInside = StepParserUtils.stripOuterParens(rowString);
            String[] ids = rowInside.split(",");

            List<String> row = new ArrayList<>();
            for (String id : ids) {
                row.add(id.trim());
            }

            grid.add(row);
        }

        return grid;
    }
}