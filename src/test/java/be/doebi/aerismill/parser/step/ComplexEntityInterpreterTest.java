package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.ComplexEntity;
import be.doebi.aerismill.model.step.base.StepEntity;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.BSplineCurveWithKnots;
import be.doebi.aerismill.model.step.geometry.BSplineSurfaceWithKnots;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ComplexEntityInterpreterTest {
    @Test
    void normalize_shouldConvertRealRationalBSplineCurveComplexEntity() {
        String rawParameters = """
                (
                BOUNDED_CURVE()
                B_SPLINE_CURVE(2,(#58067,#58068,#58069,#58070,#58071,#58072,#58073,#58074,
                #58075,#58076,#58077,#58078,#58079,#58080,#58081),.UNSPECIFIED.,.F.,.F.)
                B_SPLINE_CURVE_WITH_KNOTS((3,2,2,2,2,2,2,3),(0.105617047680327,0.107954545454545,
                0.110795454545455,0.113636363636364,0.116477272727273,0.119318181818182,
                0.122159090909091,0.125),.UNSPECIFIED.)
                CURVE()
                GEOMETRIC_REPRESENTATION_ITEM()
                RATIONAL_B_SPLINE_CURVE((0.977803170316602,0.937368138988788,1.,0.923879532511287,
                1.,0.923879532511287,1.,0.923879532511287,1.,0.923879532511287,1.,0.923879532511287,
                1.,0.923879532511287,1.))
                REPRESENTATION_ITEM('')
                )
                """;

        ComplexEntityParser parser = new ComplexEntityParser();
        StepEntity parsed = parser.parse("#932", rawParameters);
        ComplexEntityInterpreter interpreter = new ComplexEntityInterpreter();
        StepModel stepModel = new StepModel();
        stepModel.addEntity(parsed);

        interpreter.normalize(stepModel);

        StepEntity normalized = stepModel.getEntity("#932");
        assertInstanceOf(BSplineCurveWithKnots.class, normalized);

        BSplineCurveWithKnots spline = (BSplineCurveWithKnots) normalized;
        assertEquals("#932", spline.getId());
        assertEquals(2, spline.getDegree());
        assertEquals(15, spline.getControlPointRefs().size());
        assertEquals(8, spline.getKnotMultiplicities().size());
        assertEquals(15, spline.getWeights().size());
        assertEquals(0.977803170316602, spline.getWeights().getFirst());
        assertEquals(1.0, spline.getWeights().getLast());
    }

    @Test
    void normalize_shouldLeaveUnsupportedComplexEntityUntouched() {
        StepEntity parsed = new ComplexEntityParser().parse("#999", """
                (
                GEOMETRIC_REPRESENTATION_ITEM()
                REPRESENTATION_ITEM('')
                SURFACE()
                )
                """);
        assertInstanceOf(ComplexEntity.class, parsed);

        StepModel stepModel = new StepModel();
        stepModel.addEntity(parsed);

        new ComplexEntityInterpreter().normalize(stepModel);

        assertInstanceOf(ComplexEntity.class, stepModel.getEntity("#999"));
    }

    @Test
    void normalize_shouldConvertRealRationalBSplineSurfaceComplexEntity() {
        String rawParameters = """
                (
                BOUNDED_SURFACE()
                B_SPLINE_SURFACE(2,2,((#62417,#62418,#62419,#62420,#62421,#62422,#62423,
                #62424,#62425),(#62426,#62427,#62428,#62429,#62430,#62431,#62432,#62433,
                #62434),(#62435,#62436,#62437,#62438,#62439,#62440,#62441,#62442,#62443)),
                 .UNSPECIFIED.,.F.,.T.,.F.)
                B_SPLINE_SURFACE_WITH_KNOTS((3,3),(3,2,2,2,3),(1.08114908791445,1.24532093394461),
                (-3.14159265358979,-1.5707963267949,0.,1.5707963267949,3.14159265358979),
                 .UNSPECIFIED.)
                GEOMETRIC_REPRESENTATION_ITEM()
                RATIONAL_B_SPLINE_SURFACE(((1.,0.707106781186548,1.,0.707106781186548,1.,
                0.707106781186548,1.,0.707106781186548,1.),(0.996632841945465,0.704725840892859,
                0.996632841945465,0.704725840892859,0.996632841945465,0.704725840892859,
                0.996632841945465,0.704725840892859,0.996632841945465),(1.,0.707106781186548,
                1.,0.707106781186548,1.,0.707106781186548,1.,0.707106781186548,1.)))
                REPRESENTATION_ITEM('')
                SURFACE()
                )
                """;

        StepEntity parsed = new ComplexEntityParser().parse("#885", rawParameters);
        StepModel stepModel = new StepModel();
        stepModel.addEntity(parsed);

        new ComplexEntityInterpreter().normalize(stepModel);

        StepEntity normalized = stepModel.getEntity("#885");
        assertInstanceOf(BSplineSurfaceWithKnots.class, normalized);

        BSplineSurfaceWithKnots surface = (BSplineSurfaceWithKnots) normalized;
        assertEquals("#885", surface.getId());
        assertEquals(2, surface.getUDegree());
        assertEquals(2, surface.getVDegree());
        assertEquals(3, surface.getControlPointRefs().size());
        assertEquals(9, surface.getControlPointRefs().getFirst().size());
        assertEquals(3, surface.getWeights().size());
        assertEquals(9, surface.getWeights().getFirst().size());
        assertEquals(0.707106781186548, surface.getWeights().getFirst().get(1));
        assertEquals(0.996632841945465, surface.getWeights().get(1).getFirst());
    }

    @Test
    void stepParser_shouldNormalizeComplexSplineAfterLoadingModel() {
        String rawStep = """
                ISO-10303-21;
                HEADER;
                ENDSEC;
                DATA;
                #932=(
                BOUNDED_CURVE()
                B_SPLINE_CURVE(2,(#58067,#58068,#58069,#58070,#58071,#58072,#58073,#58074,
                #58075,#58076,#58077,#58078,#58079,#58080,#58081),.UNSPECIFIED.,.F.,.F.)
                B_SPLINE_CURVE_WITH_KNOTS((3,2,2,2,2,2,2,3),(0.105617047680327,0.107954545454545,
                0.110795454545455,0.113636363636364,0.116477272727273,0.119318181818182,
                0.122159090909091,0.125),.UNSPECIFIED.)
                CURVE()
                GEOMETRIC_REPRESENTATION_ITEM()
                RATIONAL_B_SPLINE_CURVE((0.977803170316602,0.937368138988788,1.,0.923879532511287,
                1.,0.923879532511287,1.,0.923879532511287,1.,0.923879532511287,1.,0.923879532511287,
                1.,0.923879532511287,1.))
                REPRESENTATION_ITEM('')
                );
                ENDSEC;
                END-ISO-10303-21;
                """;

        StepModel stepModel = new StepParser().parse(new File("test.step"), rawStep);

        assertInstanceOf(BSplineCurveWithKnots.class, stepModel.getEntity("#932"));
    }
}
