package be.doebi.aerismill.eval.step.surface;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.CylindricalSurface3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.Axis2Placement3D;
import be.doebi.aerismill.model.step.geometry.CartesianPoint;
import be.doebi.aerismill.model.step.geometry.CylindricalSurface;
import be.doebi.aerismill.model.step.geometry.Direction;
import be.doebi.aerismill.model.step.geometry.Plane;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class DefaultSurfaceEvaluatorTest {

    private static final double EPS = 1e-9;

    @Test
    void evaluatePlane_shouldBuildPlaneSurface3() {
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
        Plane plane = new Plane(
                "#50",
                "('P',#40)",
                "P",
                "#40"
        );

        model.addEntity(location);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.addEntity(plane);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        PlaneSurface3 result = surfaceEvaluator.evaluatePlane(plane);

        Point3 p = result.pointAt(2.0, 3.0);
        assertEquals(12.0, p.x(), EPS);
        assertEquals(23.0, p.y(), EPS);
        assertEquals(30.0, p.z(), EPS);

        Vec3 n = result.normalAt(0.0, 0.0);
        assertEquals(0.0, n.x(), EPS);
        assertEquals(0.0, n.y(), EPS);
        assertEquals(1.0, n.z(), EPS);
    }

    @Test
    void evaluatePlane_shouldUseCache() {
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
        Plane plane = new Plane(
                "#50",
                "('P',#40)",
                "P",
                "#40"
        );

        model.addEntity(location);
        model.addEntity(placement);
        model.addEntity(plane);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        PlaneSurface3 first = surfaceEvaluator.evaluatePlane(plane);
        PlaneSurface3 second = surfaceEvaluator.evaluatePlane(plane);

        assertSame(first, second);
    }

    @Test
    void evaluateCylindricalSurface_shouldBuildCylindricalSurface3() {
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
        CylindricalSurface cylindricalSurface = new CylindricalSurface(
                "#60",
                "('CS',#40,5.0)",
                "CS",
                "#40",
                5.0
        );

        model.addEntity(location);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.addEntity(cylindricalSurface);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        CylindricalSurface3 result = surfaceEvaluator.evaluateCylindricalSurface(cylindricalSurface);

        Point3 p = result.pointAt(0.0, 7.0);
        assertEquals(15.0, p.x(), EPS);
        assertEquals(20.0, p.y(), EPS);
        assertEquals(37.0, p.z(), EPS);

        Vec3 n = result.normalAt(0.0, 0.0);
        assertEquals(1.0, n.x(), EPS);
        assertEquals(0.0, n.y(), EPS);
        assertEquals(0.0, n.z(), EPS);
    }

    @Test
    void evaluateCylindricalSurface_shouldUseCache() {
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
        CylindricalSurface cylindricalSurface = new CylindricalSurface(
                "#60",
                "('CS',#40,3.0)",
                "CS",
                "#40",
                3.0
        );

        model.addEntity(location);
        model.addEntity(placement);
        model.addEntity(cylindricalSurface);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        CylindricalSurface3 first = surfaceEvaluator.evaluateCylindricalSurface(cylindricalSurface);
        CylindricalSurface3 second = surfaceEvaluator.evaluateCylindricalSurface(cylindricalSurface);

        assertSame(first, second);
    }
}