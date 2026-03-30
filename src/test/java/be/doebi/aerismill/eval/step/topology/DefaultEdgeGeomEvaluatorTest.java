package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.curve.CurveEvaluator;
import be.doebi.aerismill.eval.step.curve.DefaultCurveEvaluator;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Circle;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Line;
import be.doebi.aerismill.model.step.geometry.Vector;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.topology.EdgeCurve;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultEdgeGeomEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluateEdge_shouldBuildEdgeGeom_forLine() {
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

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(direction);
        model.addEntity(vector);
        model.addEntity(line);
        model.addEntity(edgeCurve);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        DefaultEdgeGeomEvaluator evaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);

        EdgeGeom result = evaluator.evaluateEdge(edgeCurve);

        assertEquals("#50", result.stepId());
        assertEquals(0.0, result.startPoint().x(), EPS);
        assertEquals(0.0, result.startPoint().y(), EPS);
        assertEquals(0.0, result.startPoint().z(), EPS);
        assertEquals(5.0, result.endPoint().x(), EPS);
        assertEquals(0.0, result.endPoint().y(), EPS);
        assertEquals(0.0, result.endPoint().z(), EPS);
        assertEquals(true, result.sameSense());
    }

    @Test
    void evaluateEdge_shouldBuildEdgeGeom_forCircle() {
        StepModel model = new StepModel();

        CartesianPoint center = new CartesianPoint("#10", "('O',(0.0,0.0,0.0))", "O", List.of(0.0, 0.0, 0.0));
        CartesianPoint p1 = new CartesianPoint("#11", "('P1',(5.0,0.0,0.0))", "P1", List.of(5.0, 0.0, 0.0));
        CartesianPoint p2 = new CartesianPoint("#12", "('P2',(0.0,5.0,0.0))", "P2", List.of(0.0, 5.0, 0.0));

        VertexPoint v1 = new VertexPoint("#20", "('V1',#11)", "V1", "#11");
        VertexPoint v2 = new VertexPoint("#21", "('V2',#12)", "V2", "#12");

        Direction axis = new Direction("#30", "('Z',(0.0,0.0,1.0))", "Z", List.of(0.0, 0.0, 1.0));
        Direction refDirection = new Direction("#31", "('X',(1.0,0.0,0.0))", "X", List.of(1.0, 0.0, 0.0));
        Axis2Placement3D placement = new Axis2Placement3D("#40", "('A',#10,#30,#31)", "A", "#10", "#30", "#31");
        Circle circle = new Circle("#41", "('C',#40,5.0)", "C", "#40", 5.0);

        EdgeCurve edgeCurve = new EdgeCurve(
                "#50",
                "('E',#20,#21,#41,.T.)",
                "E",
                "#20",
                "#21",
                "#41",
                true
        );

        model.addEntity(center);
        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.addEntity(circle);
        model.addEntity(edgeCurve);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        DefaultEdgeGeomEvaluator evaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);

        EdgeGeom result = evaluator.evaluateEdge(edgeCurve);

        assertEquals(5.0, result.startPoint().x(), EPS);
        assertEquals(0.0, result.startPoint().y(), EPS);
        assertEquals(0.0, result.startPoint().z(), EPS);
        assertEquals(0.0, result.endPoint().x(), EPS);
        assertEquals(5.0, result.endPoint().y(), EPS);
        assertEquals(0.0, result.endPoint().z(), EPS);
    }

    @Test
    void evaluateEdge_shouldUseCache() {
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

        model.addEntity(p1);
        model.addEntity(p2);
        model.addEntity(v1);
        model.addEntity(v2);
        model.addEntity(direction);
        model.addEntity(vector);
        model.addEntity(line);
        model.addEntity(edgeCurve);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);
        VertexGeomEvaluator vertexGeomEvaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);
        DefaultEdgeGeomEvaluator evaluator = new DefaultEdgeGeomEvaluator(context, curveEvaluator, vertexGeomEvaluator);

        EdgeGeom first = evaluator.evaluateEdge(edgeCurve);
        EdgeGeom second = evaluator.evaluateEdge(edgeCurve);

        assertSame(first, second);
    }
}