package be.doebi.aerismill.eval.step.topology;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.topology.VertexGeom;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.topology.VertexPoint;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultVertexGeomEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluateVertex_shouldBuildVertexGeom() {
        StepModel model = new StepModel();

        CartesianPoint point = new CartesianPoint(
                "#10",
                "('P',(1.0,2.0,3.0))",
                "P",
                List.of(1.0, 2.0, 3.0)
        );
        VertexPoint vertexPoint = new VertexPoint(
                "#20",
                "('V',#10)",
                "V",
                "#10"
        );

        model.addEntity(point);
        model.addEntity(vertexPoint);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultVertexGeomEvaluator evaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);

        VertexGeom result = evaluator.evaluateVertex(vertexPoint);

        assertEquals("#20", result.stepId());
        assertEquals(1.0, result.position().x(), EPS);
        assertEquals(2.0, result.position().y(), EPS);
        assertEquals(3.0, result.position().z(), EPS);
    }

    @Test
    void evaluateVertex_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint point = new CartesianPoint(
                "#10",
                "('P',(4.0,5.0,6.0))",
                "P",
                List.of(4.0, 5.0, 6.0)
        );
        VertexPoint vertexPoint = new VertexPoint(
                "#20",
                "('V',#10)",
                "V",
                "#10"
        );

        model.addEntity(point);
        model.addEntity(vertexPoint);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultVertexGeomEvaluator evaluator = new DefaultVertexGeomEvaluator(context, placementEvaluator);

        VertexGeom first = evaluator.evaluateVertex(vertexPoint);
        VertexGeom second = evaluator.evaluateVertex(vertexPoint);

        assertSame(first, second);
    }
}