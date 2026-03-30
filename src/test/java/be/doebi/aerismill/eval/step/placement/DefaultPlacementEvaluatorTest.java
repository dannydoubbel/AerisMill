package be.doebi.aerismill.eval.step.placement;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Vector;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultPlacementEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluatePoint_shouldReturnPoint3() {
        StepModel model = new StepModel();
        CartesianPoint point = new CartesianPoint(
                "#10",
                "('P',(1.0,2.0,3.0))",
                "P",
                List.of(1.0, 2.0, 3.0)
        );
        model.addEntity(point);

        DefaultPlacementEvaluator evaluator = new DefaultPlacementEvaluator(
                new StepEvaluationContext(model)
        );

        Point3 result = evaluator.evaluatePoint(point);

        assertEquals(1.0, result.x(), EPS);
        assertEquals(2.0, result.y(), EPS);
        assertEquals(3.0, result.z(), EPS);
    }

    @Test
    void evaluatePoint_shouldUseCache() {
        StepModel model = new StepModel();
        CartesianPoint point = new CartesianPoint(
                "#10",
                "('P',(1.0,2.0,3.0))",
                "P",
                List.of(1.0, 2.0, 3.0)
        );
        model.addEntity(point);

        DefaultPlacementEvaluator evaluator = new DefaultPlacementEvaluator(
                new StepEvaluationContext(model)
        );

        Point3 first = evaluator.evaluatePoint(point);
        Point3 second = evaluator.evaluatePoint(point);

        assertSame(first, second);
    }

    @Test
    void evaluateDirection_shouldNormalize() {
        StepModel model = new StepModel();
        Direction direction = new Direction(
                "#20",
                "('D',(0.0,0.0,10.0))",
                "D",
                List.of(0.0, 0.0, 10.0)
        );
        model.addEntity(direction);

        DefaultPlacementEvaluator evaluator = new DefaultPlacementEvaluator(
                new StepEvaluationContext(model)
        );

        UnitVec3 result = evaluator.evaluateDirection(direction);

        assertEquals(0.0, result.x(), EPS);
        assertEquals(0.0, result.y(), EPS);
        assertEquals(1.0, result.z(), EPS);
    }

    @Test
    void evaluateVector_shouldApplyMagnitude() {
        StepModel model = new StepModel();

        Direction direction = new Direction(
                "#20",
                "('D',(1.0,0.0,0.0))",
                "D",
                List.of(1.0, 0.0, 0.0)
        );
        Vector vector = new Vector(
                "#30",
                "('V',#20,5.0)",
                "V",
                "#20",
                5.0
        );

        model.addEntity(direction);
        model.addEntity(vector);
        model.resolveAll();

        DefaultPlacementEvaluator evaluator = new DefaultPlacementEvaluator(
                new StepEvaluationContext(model)
        );

        Vec3 result = evaluator.evaluateVector(vector);

        assertEquals(5.0, result.x(), EPS);
        assertEquals(0.0, result.y(), EPS);
        assertEquals(0.0, result.z(), EPS);
    }

    @Test
    void evaluateAxis2Placement3D_shouldBuildFrame() {
        StepModel model = new StepModel();

        CartesianPoint location = new CartesianPoint(
                "#10",
                "('O',(10.0,20.0,30.0))",
                "O",
                List.of(10.0, 20.0, 30.0)
        );
        Direction axis = new Direction(
                "#20",
                "('Z',(0.0,0.0,1.0))",
                "Z",
                List.of(0.0, 0.0, 1.0)
        );
        Direction refDirection = new Direction(
                "#21",
                "('X',(1.0,0.0,0.0))",
                "X",
                List.of(1.0, 0.0, 0.0)
        );
        Axis2Placement3D placement = new Axis2Placement3D(
                "#40",
                "('A',#10,#20,#21)",
                "A",
                "#10",
                "#20",
                "#21"
        );

        model.addEntity(location);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.resolveAll();

        DefaultPlacementEvaluator evaluator = new DefaultPlacementEvaluator(
                new StepEvaluationContext(model)
        );

        Frame3 result = evaluator.evaluateAxis2Placement3D(placement);

        assertEquals(10.0, result.origin().x(), EPS);
        assertEquals(20.0, result.origin().y(), EPS);
        assertEquals(30.0, result.origin().z(), EPS);

        assertEquals(1.0, result.xAxis().x(), EPS);
        assertEquals(0.0, result.xAxis().y(), EPS);
        assertEquals(0.0, result.xAxis().z(), EPS);

        assertEquals(0.0, result.yAxis().x(), EPS);
        assertEquals(1.0, result.yAxis().y(), EPS);
        assertEquals(0.0, result.yAxis().z(), EPS);

        assertEquals(0.0, result.zAxis().x(), EPS);
        assertEquals(0.0, result.zAxis().y(), EPS);
        assertEquals(1.0, result.zAxis().z(), EPS);
    }

    @Test
    void evaluateAxis2Placement3D_shouldUseDefaultAxesWhenOptionalRefsAreMissing() {
        StepModel model = new StepModel();

        CartesianPoint location = new CartesianPoint(
                "#10",
                "('O',(1.0,2.0,3.0))",
                "O",
                List.of(1.0, 2.0, 3.0)
        );
        Axis2Placement3D placement = new Axis2Placement3D(
                "#40",
                "('A',#10,$,$)",
                "A",
                "#10",
                "$",
                "$"
        );

        model.addEntity(location);
        model.addEntity(placement);
        model.resolveAll();

        DefaultPlacementEvaluator evaluator = new DefaultPlacementEvaluator(
                new StepEvaluationContext(model)
        );

        Frame3 result = evaluator.evaluateAxis2Placement3D(placement);

        assertEquals(1.0, result.origin().x(), EPS);
        assertEquals(2.0, result.origin().y(), EPS);
        assertEquals(3.0, result.origin().z(), EPS);

        assertEquals(1.0, result.xAxis().x(), EPS);
        assertEquals(0.0, result.xAxis().y(), EPS);
        assertEquals(0.0, result.xAxis().z(), EPS);

        assertEquals(0.0, result.yAxis().x(), EPS);
        assertEquals(1.0, result.yAxis().y(), EPS);
        assertEquals(0.0, result.yAxis().z(), EPS);

        assertEquals(0.0, result.zAxis().x(), EPS);
        assertEquals(0.0, result.zAxis().y(), EPS);
        assertEquals(1.0, result.zAxis().z(), EPS);
    }
}