package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.curve.CurveEvaluator;
import be.doebi.aerismill.eval.step.curve.DefaultCurveEvaluator;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.OrientedEdge;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultOrientedEdgeGeomEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluateOrientedEdge_shouldBuildOrientedEdgeGeom_withTrueOrientation() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(5.0,0.0,0.0))", "P2", List.of(5.0, 0.0, 0.0));
        VertexPoint v1 = new VertexPoint("#20", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#21", "('V2',#11)", "V2", "#11");

        Direction direction = new Direction("#30", "('D',(1.0,0.0,0.0))", "D", List.of(1.0, 0.0, 0.0));
        Vector vector = new Vector("#31", "('V',#30,5.0)", "V", "#30", 5.0);
        Line line = new Line("#40", "('L',#10,#31)", "L", "#10", "#31");

        EdgeCurve edgeCurve = new EdgeCurve(
                "#50",
                "('E',#20,#21,#40,.T.)",
                "E",
                "#20",
                "#21",
                "#40",
                true
        );

        OrientedEdge orientedEdge = new OrientedEdge(
                "#60",
                "('OE',*,*,#50,.T.)",
                "OE",
                "*",
                "*",
                "#50",
                true
        );

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(direction);
        model.addEntity(vector);
        model.addEntity(line);
        model.addEntity(edgeCurve);
        model.addEntity(orientedEdge);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        DefaultOrientedEdgeGeomEvaluator evaluator = new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);

        OrientedEdgeGeom result = evaluator.evaluateOrientedEdge(orientedEdge);

        assertEquals("#60", result.stepId());
        assertEquals(0.0, result.start().x(), EPS);
        assertEquals(0.0, result.start().y(), EPS);
        assertEquals(0.0, result.start().z(), EPS);
        assertEquals(5.0, result.end().x(), EPS);
        assertEquals(0.0, result.end().y(), EPS);
        assertEquals(0.0, result.end().z(), EPS);
    }

    @Test
    void evaluateOrientedEdge_shouldBuildOrientedEdgeGeom_withFalseOrientation() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(5.0,0.0,0.0))", "P2", List.of(5.0, 0.0, 0.0));
        VertexPoint v1 = new VertexPoint("#20", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#21", "('V2',#11)", "V2", "#11");

        Direction direction = new Direction("#30", "('D',(1.0,0.0,0.0))", "D", List.of(1.0, 0.0, 0.0));
        Vector vector = new Vector("#31", "('V',#30,5.0)", "V", "#30", 5.0);
        Line line = new Line("#40", "('L',#10,#31)", "L", "#10", "#31");

        EdgeCurve edgeCurve = new EdgeCurve(
                "#50",
                "('E',#20,#21,#40,.T.)",
                "E",
                "#20",
                "#21",
                "#40",
                true
        );

        OrientedEdge orientedEdge = new OrientedEdge(
                "#60",
                "('OE',*,*,#50,.F.)",
                "OE",
                "*",
                "*",
                "#50",
                false
        );

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(direction);
        model.addEntity(vector);
        model.addEntity(line);
        model.addEntity(edgeCurve);
        model.addEntity(orientedEdge);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        DefaultOrientedEdgeGeomEvaluator evaluator = new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);

        OrientedEdgeGeom result = evaluator.evaluateOrientedEdge(orientedEdge);

        assertEquals(5.0, result.start().x(), EPS);
        assertEquals(0.0, result.start().y(), EPS);
        assertEquals(0.0, result.start().z(), EPS);
        assertEquals(0.0, result.end().x(), EPS);
        assertEquals(0.0, result.end().y(), EPS);
        assertEquals(0.0, result.end().z(), EPS);
    }

    @Test
    void evaluateOrientedEdge_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(1.0,0.0,0.0))", "P2", List.of(1.0, 0.0, 0.0));
        VertexPoint v1 = new VertexPoint("#20", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#21", "('V2',#11)", "V2", "#11");

        Direction direction = new Direction("#30", "('D',(1.0,0.0,0.0))", "D", List.of(1.0, 0.0, 0.0));
        Vector vector = new Vector("#31", "('V',#30,1.0)", "V", "#30", 1.0);
        Line line = new Line("#40", "('L',#10,#31)", "L", "#10", "#31");

        EdgeCurve edgeCurve = new EdgeCurve(
                "#50",
                "('E',#20,#21,#40,.T.)",
                "E",
                "#20",
                "#21",
                "#40",
                true
        );

        OrientedEdge orientedEdge = new OrientedEdge(
                "#60",
                "('OE',*,*,#50,.T.)",
                "OE",
                "*",
                "*",
                "#50",
                true
        );

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(direction);
        model.addEntity(vector);
        model.addEntity(line);
        model.addEntity(edgeCurve);
        model.addEntity(orientedEdge);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        DefaultOrientedEdgeGeomEvaluator evaluator = new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);

        OrientedEdgeGeom first = evaluator.evaluateOrientedEdge(orientedEdge);
        OrientedEdgeGeom second = evaluator.evaluateOrientedEdge(orientedEdge);

        assertSame(first, second);
    }


    @Test
    void evaluateOrientedEdge_shouldUseForwardDirection_whenOrientationIsTrue() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(5.0,0.0,0.0))", "P2", List.of(5.0, 0.0, 0.0));

        Direction dx = new Direction("#20", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));
        Vector vx = new Vector("#21", "('VX',#20,5.0)", "VX", "#20", 5.0);
        Line line = new Line("#22", "('L1',#10,#21)", "L1", "#10", "#21");

        VertexPoint v1 = new VertexPoint("#30", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#31", "('V2',#11)", "V2", "#11");

        EdgeCurve edgeCurve = new EdgeCurve("#40", "('E1',#30,#31,#22,.T.)", "E1", "#30", "#31", "#22", true);
        OrientedEdge orientedEdge = new OrientedEdge("#50", "('OE1',*,*,#40,.T.)", "OE1", null, null, "#40", true);

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(dx);
        model.addEntity(vx);
        model.addEntity(line);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(edgeCurve);
        model.addEntity(orientedEdge);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        DefaultOrientedEdgeGeomEvaluator evaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);

        OrientedEdgeGeom result = evaluator.evaluateOrientedEdge(orientedEdge);

        assertEquals("#50", result.stepId());
        assertEquals(0.0, result.start().x(), EPS);
        assertEquals(0.0, result.start().y(), EPS);
        assertEquals(0.0, result.start().z(), EPS);

        assertEquals(5.0, result.end().x(), EPS);
        assertEquals(0.0, result.end().y(), EPS);
        assertEquals(0.0, result.end().z(), EPS);
    }


    @Test
    void evaluateOrientedEdge_shouldReverseDirection_whenOrientationIsFalse() {
        StepModel model = new StepModel();

        CartesianPoint p1 = new CartesianPoint("#10", "('P1',(0.0,0.0,0.0))", "P1", List.of(0.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#11", "('P2',(5.0,0.0,0.0))", "P2", List.of(5.0, 0.0, 0.0));

        Direction dx = new Direction("#20", "('DX',(1.0,0.0,0.0))", "DX", List.of(1.0, 0.0, 0.0));
        Vector vx = new Vector("#21", "('VX',#20,5.0)", "VX", "#20", 5.0);
        Line line = new Line("#22", "('L1',#10,#21)", "L1", "#10", "#21");

        VertexPoint v1 = new VertexPoint("#30", "('V1',#10)", "V1", "#10");
        VertexPoint v2 = new VertexPoint("#31", "('V2',#11)", "V2", "#11");

        EdgeCurve edgeCurve = new EdgeCurve("#40", "('E1',#30,#31,#22,.T.)", "E1", "#30", "#31", "#22", true);
        OrientedEdge orientedEdge = new OrientedEdge("#50", "('OE1',*,*,#40,.F.)", "OE1", null, null, "#40", false);

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(dx);
        model.addEntity(vx);
        model.addEntity(line);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(edgeCurve);
        model.addEntity(orientedEdge);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        EdgeGeomEvaluator edgeGeomEvaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);
        DefaultOrientedEdgeGeomEvaluator evaluator =
                new DefaultOrientedEdgeGeomEvaluator(context, edgeGeomEvaluator);

        OrientedEdgeGeom result = evaluator.evaluateOrientedEdge(orientedEdge);

        assertEquals("#50", result.stepId());
        assertEquals(5.0, result.start().x(), EPS);
        assertEquals(0.0, result.start().y(), EPS);
        assertEquals(0.0, result.start().z(), EPS);

        assertEquals(0.0, result.end().x(), EPS);
        assertEquals(0.0, result.end().y(), EPS);
        assertEquals(0.0, result.end().z(), EPS);
    }
}