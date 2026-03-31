package be.doebi.aerismill.eval.step.curve;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.curve.CircleCurve3;
import be.doebi.aerismill.model.geom.curve.EllipseCurve3;
import be.doebi.aerismill.model.geom.curve.LineCurve3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCurveEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluateLine_shouldBuildLineCurve3() {
        StepModel model = new StepModel();

        CartesianPoint point = new CartesianPoint(
                "#10",
                "('P',(1.0,2.0,3.0))",
                "P",
                List.of(1.0, 2.0, 3.0)
        );
        Direction direction = new Direction(
                "#20",
                "('D',(10.0,0.0,0.0))",
                "D",
                List.of(10.0, 0.0, 0.0)
        );
        Vector vector = new Vector(
                "#30",
                "('V',#20,5.0)",
                "V",
                "#20",
                5.0
        );
        Line line = new Line(
                "#40",
                "('L',#10,#30)",
                "L",
                "#10",
                "#30"
        );

        model.addEntity(point);
        model.addEntity(direction);
        model.addEntity(vector);
        model.addEntity(line);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultCurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);

        LineCurve3 result = curveEvaluator.evaluateLine(line);

        assertEquals(1.0, result.origin().x(), EPS);
        assertEquals(2.0, result.origin().y(), EPS);
        assertEquals(3.0, result.origin().z(), EPS);

        assertEquals(1.0, result.direction().x(), EPS);
        assertEquals(0.0, result.direction().y(), EPS);
        assertEquals(0.0, result.direction().z(), EPS);
    }

    @Test
    void evaluateLine_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint point = new CartesianPoint(
                "#10",
                "('P',(0.0,0.0,0.0))",
                "P",
                List.of(0.0, 0.0, 0.0)
        );
        Direction direction = new Direction(
                "#20",
                "('D',(1.0,0.0,0.0))",
                "D",
                List.of(1.0, 0.0, 0.0)
        );
        Vector vector = new Vector(
                "#30",
                "('V',#20,2.0)",
                "V",
                "#20",
                2.0
        );
        Line line = new Line(
                "#40",
                "('L',#10,#30)",
                "L",
                "#10",
                "#30"
        );

        model.addEntity(point);
        model.addEntity(direction);
        model.addEntity(vector);
        model.addEntity(line);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultCurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);

        LineCurve3 first = curveEvaluator.evaluateLine(line);
        LineCurve3 second = curveEvaluator.evaluateLine(line);

        assertSame(first, second);
    }

    @Test
    void evaluateCircle_shouldBuildCircleCurve3() {
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
        Circle circle = new Circle(
                "#50",
                "('C',#40,5.0)",
                "C",
                "#40",
                5.0
        );

        model.addEntity(location);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.addEntity(circle);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultCurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);

        CircleCurve3 result = curveEvaluator.evaluateCircle(circle);

        assertEquals(10.0, result.frame().origin().x(), EPS);
        assertEquals(20.0, result.frame().origin().y(), EPS);
        assertEquals(30.0, result.frame().origin().z(), EPS);
        assertEquals(5.0, result.radius(), EPS);

        Point3 p0 = result.pointAt(0.0);
        assertEquals(15.0, p0.x(), EPS);
        assertEquals(20.0, p0.y(), EPS);
        assertEquals(30.0, p0.z(), EPS);

        Vec3 t0 = result.tangentAt(0.0);
        assertEquals(0.0, t0.x(), EPS);
        assertEquals(5.0, t0.y(), EPS);
        assertEquals(0.0, t0.z(), EPS);
    }

    @Test
    void evaluateCircle_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint location = new CartesianPoint(
                "#10",
                "('O',(0.0,0.0,0.0))",
                "O",
                List.of(0.0, 0.0, 0.0)
        );
        Axis2Placement3D placement = new Axis2Placement3D(
                "#40",
                "('A',#10,$,$)",
                "A",
                "#10",
                "$",
                "$"
        );
        Circle circle = new Circle(
                "#50",
                "('C',#40,3.0)",
                "C",
                "#40",
                3.0
        );

        model.addEntity(location);
        model.addEntity(placement);
        model.addEntity(circle);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultCurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);

        CircleCurve3 first = curveEvaluator.evaluateCircle(circle);
        CircleCurve3 second = curveEvaluator.evaluateCircle(circle);

        assertSame(first, second);
    }

    @Test
    void evaluateEllipse_shouldCreateEllipseCurve3() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint(
                "#10",
                "( 'NONE', ( 1.0, 2.0, 3.0 ) )",
                "NONE",
                List.of(1.0, 2.0, 3.0)
        );

        Direction axis = new Direction(
                "#11",
                "( 'NONE', ( 0.0, 0.0, 1.0 ) )",
                "NONE",
                List.of(0.0, 0.0, 1.0)
        );

        Direction refDirection = new Direction(
                "#12",
                "( 'NONE', ( 1.0, 0.0, 0.0 ) )",
                "NONE",
                List.of(1.0, 0.0, 0.0)
        );

        Axis2Placement3D placement = new Axis2Placement3D(
                "#13",
                "( 'NONE', #10, #11, #12 )",
                "NONE",
                "#10",
                "#11",
                "#12"
        );

        Ellipse ellipse = new Ellipse(
                "#20",
                "( 'NONE', #13, 5.0, 3.0 )",
                "NONE",
                "#13",
                5.0,
                3.0
        );

        model.addEntity(origin);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.addEntity(ellipse);

        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        CurveEvaluator curveEvaluator = new DefaultCurveEvaluator(context, placementEvaluator);

        EllipseCurve3 result = curveEvaluator.evaluateEllipse(ellipse);

        assertNotNull(result);
        assertEquals(5.0, result.semiAxis1());
        assertEquals(3.0, result.semiAxis2());

        Point3 p0 = result.pointAt(0.0);
        assertEquals(6.0, p0.x(), 1e-9);
        assertEquals(2.0, p0.y(), 1e-9);
        assertEquals(3.0, p0.z(), 1e-9);

        Point3 p90 = result.pointAt(Math.PI / 2.0);
        assertEquals(1.0, p90.x(), 1e-9);
        assertEquals(5.0, p90.y(), 1e-9);
        assertEquals(3.0, p90.z(), 1e-9);
    }



}