package be.doebi.aerismill.eval.step.surface;

import be.doebi.aerismill.eval.step.context.StepEvaluationContext;
import be.doebi.aerismill.eval.step.placement.DefaultPlacementEvaluator;
import be.doebi.aerismill.eval.step.placement.PlacementEvaluator;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.*;
import be.doebi.aerismill.model.step.base.StepLogical;
import be.doebi.aerismill.model.step.base.StepModel;
import be.doebi.aerismill.model.step.geometry.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void evaluateConicalSurface_shouldCreateConicalSurface3() {
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

        ConicalSurface conicalSurface = new ConicalSurface(
                "#20",
                "( 'NONE', #13, 5.0, 0.5235987755982988 )",
                "NONE",
                "#13",
                5.0,
                Math.PI / 6.0
        );

        model.addEntity(origin);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.addEntity(conicalSurface);

        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        SurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        ConicalSurface3 result = surfaceEvaluator.evaluateConicalSurface(conicalSurface);

        assertNotNull(result);
        assertEquals(5.0, result.radius(), 1e-9);
        assertEquals(Math.PI / 6.0, result.semiAngle(), 1e-9);

        Point3 p0 = result.pointAt(0.0, 0.0);
        assertEquals(6.0, p0.x(), 1e-9);
        assertEquals(2.0, p0.y(), 1e-9);
        assertEquals(3.0, p0.z(), 1e-9);

        Point3 p1 = result.pointAt(0.0, 2.0);
        assertEquals(1.0 + 5.0 + 2.0 * Math.tan(Math.PI / 6.0), p1.x(), 1e-9);
        assertEquals(2.0, p1.y(), 1e-9);
        assertEquals(5.0, p1.z(), 1e-9);
    }

    @Test
    void evaluateBSplineSurfaceWithKnots_shouldCreateBilinearSurface() {
        StepModel model = new StepModel();

        CartesianPoint p00 = new CartesianPoint("#10", "('P00',(0.0,0.0,0.0))", "P00", List.of(0.0, 0.0, 0.0));
        CartesianPoint p01 = new CartesianPoint("#11", "('P01',(0.0,10.0,0.0))", "P01", List.of(0.0, 10.0, 0.0));
        CartesianPoint p10 = new CartesianPoint("#12", "('P10',(10.0,0.0,0.0))", "P10", List.of(10.0, 0.0, 0.0));
        CartesianPoint p11 = new CartesianPoint("#13", "('P11',(10.0,10.0,10.0))", "P11", List.of(10.0, 10.0, 10.0));

        BSplineSurfaceWithKnots surface = new BSplineSurfaceWithKnots(
                "#20",
                "('S',1,1,((#10,#11),(#12,#13)),.UNSPECIFIED.,.F.,.F.,.F.,(2,2),(2,2),(0.0,1.0),(0.0,1.0),.UNSPECIFIED.)",
                "S",
                1,
                1,
                List.of(
                        List.of("#10", "#11"),
                        List.of("#12", "#13")
                ),
                ".UNSPECIFIED.",
                StepLogical.FALSE,
                StepLogical.FALSE,
                StepLogical.FALSE,
                List.of(2, 2),
                List.of(2, 2),
                List.of(0.0, 1.0),
                List.of(0.0, 1.0),
                ".UNSPECIFIED."
        );

        model.addEntity(p00);
        model.addEntity(p01);
        model.addEntity(p10);
        model.addEntity(p11);
        model.addEntity(surface);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        BSplineSurface3 result = surfaceEvaluator.evaluateBSplineSurfaceWithKnots(surface);

        assertNotNull(result);
        assertEquals(1, result.uDegree());
        assertEquals(1, result.vDegree());
        assertEquals(List.of(0.0, 0.0, 1.0, 1.0), result.uKnots());
        assertEquals(List.of(0.0, 0.0, 1.0, 1.0), result.vKnots());

        Point3 p = result.pointAt(0.5, 0.5);
        assertEquals(5.0, p.x(), EPS);
        assertEquals(5.0, p.y(), EPS);
        assertEquals(2.5, p.z(), EPS);
    }

    @Test
    void evaluateBSplineSurfaceWithKnots_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint p00 = new CartesianPoint("#10", "('P00',(0.0,0.0,0.0))", "P00", List.of(0.0, 0.0, 0.0));
        CartesianPoint p01 = new CartesianPoint("#11", "('P01',(0.0,10.0,0.0))", "P01", List.of(0.0, 10.0, 0.0));
        CartesianPoint p10 = new CartesianPoint("#12", "('P10',(10.0,0.0,0.0))", "P10", List.of(10.0, 0.0, 0.0));
        CartesianPoint p11 = new CartesianPoint("#13", "('P11',(10.0,10.0,10.0))", "P11", List.of(10.0, 10.0, 10.0));

        BSplineSurfaceWithKnots surface = new BSplineSurfaceWithKnots(
                "#20",
                "('S',1,1,((#10,#11),(#12,#13)),.UNSPECIFIED.,.F.,.F.,.F.,(2,2),(2,2),(0.0,1.0),(0.0,1.0),.UNSPECIFIED.)",
                "S",
                1,
                1,
                List.of(
                        List.of("#10", "#11"),
                        List.of("#12", "#13")
                ),
                ".UNSPECIFIED.",
                StepLogical.FALSE,
                StepLogical.FALSE,
                StepLogical.FALSE,
                List.of(2, 2),
                List.of(2, 2),
                List.of(0.0, 1.0),
                List.of(0.0, 1.0),
                ".UNSPECIFIED."
        );

        model.addEntity(p00);
        model.addEntity(p01);
        model.addEntity(p10);
        model.addEntity(p11);
        model.addEntity(surface);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        BSplineSurface3 first = surfaceEvaluator.evaluateBSplineSurfaceWithKnots(surface);
        BSplineSurface3 second = surfaceEvaluator.evaluateBSplineSurfaceWithKnots(surface);

        assertSame(first, second);
    }

    @Test
    void evaluateBSplineSurfaceWithKnots_shouldEvaluateCubicSurface() {
        StepModel model = new StepModel();

        CartesianPoint p00 = new CartesianPoint("#10", "('P00',(0.0,0.0,0.0))", "P00", List.of(0.0, 0.0, 0.0));
        CartesianPoint p01 = new CartesianPoint("#11", "('P01',(0.0,10.0,0.0))", "P01", List.of(0.0, 10.0, 0.0));
        CartesianPoint p02 = new CartesianPoint("#12", "('P02',(0.0,20.0,0.0))", "P02", List.of(0.0, 20.0, 0.0));
        CartesianPoint p03 = new CartesianPoint("#13", "('P03',(0.0,30.0,0.0))", "P03", List.of(0.0, 30.0, 0.0));

        CartesianPoint p10 = new CartesianPoint("#20", "('P10',(10.0,0.0,0.0))", "P10", List.of(10.0, 0.0, 0.0));
        CartesianPoint p11 = new CartesianPoint("#21", "('P11',(10.0,10.0,10.0))", "P11", List.of(10.0, 10.0, 10.0));
        CartesianPoint p12 = new CartesianPoint("#22", "('P12',(10.0,20.0,10.0))", "P12", List.of(10.0, 20.0, 10.0));
        CartesianPoint p13 = new CartesianPoint("#23", "('P13',(10.0,30.0,0.0))", "P13", List.of(10.0, 30.0, 0.0));

        CartesianPoint p20 = new CartesianPoint("#30", "('P20',(20.0,0.0,0.0))", "P20", List.of(20.0, 0.0, 0.0));
        CartesianPoint p21 = new CartesianPoint("#31", "('P21',(20.0,10.0,10.0))", "P21", List.of(20.0, 10.0, 10.0));
        CartesianPoint p22 = new CartesianPoint("#32", "('P22',(20.0,20.0,10.0))", "P22", List.of(20.0, 20.0, 10.0));
        CartesianPoint p23 = new CartesianPoint("#33", "('P23',(20.0,30.0,0.0))", "P23", List.of(20.0, 30.0, 0.0));

        CartesianPoint p30 = new CartesianPoint("#40", "('P30',(30.0,0.0,0.0))", "P30", List.of(30.0, 0.0, 0.0));
        CartesianPoint p31 = new CartesianPoint("#41", "('P31',(30.0,10.0,0.0))", "P31", List.of(30.0, 10.0, 0.0));
        CartesianPoint p32 = new CartesianPoint("#42", "('P32',(30.0,20.0,0.0))", "P32", List.of(30.0, 20.0, 0.0));
        CartesianPoint p33 = new CartesianPoint("#43", "('P33',(30.0,30.0,0.0))", "P33", List.of(30.0, 30.0, 0.0));

        BSplineSurfaceWithKnots surface = new BSplineSurfaceWithKnots(
                "#50",
                "('S',3,3,((#10,#11,#12,#13),(#20,#21,#22,#23),(#30,#31,#32,#33),(#40,#41,#42,#43)),.UNSPECIFIED.,.F.,.F.,.F.,(4,4),(4,4),(0.0,1.0),(0.0,1.0),.UNSPECIFIED.)",
                "S",
                3,
                3,
                List.of(
                        List.of("#10", "#11", "#12", "#13"),
                        List.of("#20", "#21", "#22", "#23"),
                        List.of("#30", "#31", "#32", "#33"),
                        List.of("#40", "#41", "#42", "#43")
                ),
                ".UNSPECIFIED.",
                StepLogical.FALSE,
                StepLogical.FALSE,
                StepLogical.FALSE,
                List.of(4, 4),
                List.of(4, 4),
                List.of(0.0, 1.0),
                List.of(0.0, 1.0),
                ".UNSPECIFIED."
        );

        model.addEntity(p00);
        model.addEntity(p01);
        model.addEntity(p02);
        model.addEntity(p03);
        model.addEntity(p10);
        model.addEntity(p11);
        model.addEntity(p12);
        model.addEntity(p13);
        model.addEntity(p20);
        model.addEntity(p21);
        model.addEntity(p22);
        model.addEntity(p23);
        model.addEntity(p30);
        model.addEntity(p31);
        model.addEntity(p32);
        model.addEntity(p33);
        model.addEntity(surface);

        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        BSplineSurface3 result = surfaceEvaluator.evaluateBSplineSurfaceWithKnots(surface);

        assertNotNull(result);
        assertEquals(3, result.uDegree());
        assertEquals(3, result.vDegree());
        assertEquals(List.of(0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0), result.uKnots());
        assertEquals(List.of(0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0), result.vKnots());

        Point3 p00Result = result.pointAt(0.0, 0.0);
        assertEquals(0.0, p00Result.x(), EPS);
        assertEquals(0.0, p00Result.y(), EPS);
        assertEquals(0.0, p00Result.z(), EPS);

        Point3 p11Result = result.pointAt(1.0, 1.0);
        assertEquals(30.0, p11Result.x(), 1e-6);
        assertEquals(30.0, p11Result.y(), 1e-6);
        assertEquals(0.0, p11Result.z(), 1e-6);

        Point3 mid = result.pointAt(0.5, 0.5);
        assertTrue(mid.x() > 0.0 && mid.x() < 30.0);
        assertTrue(mid.y() > 0.0 && mid.y() < 30.0);
        assertTrue(mid.z() > 0.0);
    }

    @Test
    void evaluateSphericalSurface_shouldCreateSphericalSurface3() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint(
                "#10",
                "('O',(1.0,2.0,3.0))",
                "O",
                List.of(1.0, 2.0, 3.0)
        );
        Direction axis = new Direction(
                "#11",
                "('Z',(0.0,0.0,1.0))",
                "Z",
                List.of(0.0, 0.0, 1.0)
        );
        Direction refDirection = new Direction(
                "#12",
                "('X',(1.0,0.0,0.0))",
                "X",
                List.of(1.0, 0.0, 0.0)
        );
        Axis2Placement3D placement = new Axis2Placement3D(
                "#13",
                "('A',#10,#11,#12)",
                "A",
                "#10",
                "#11",
                "#12"
        );
        SphericalSurface sphericalSurface = new SphericalSurface(
                "#20",
                "('S',#13,5.0)",
                "S",
                "#13",
                5.0
        );

        model.addEntity(origin);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.addEntity(sphericalSurface);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        SphericalSurface3 result = surfaceEvaluator.evaluateSphericalSurface(sphericalSurface);

        assertNotNull(result);
        assertEquals(5.0, result.radius(), EPS);

        Point3 p0 = result.pointAt(0.0, 0.0);
        assertEquals(6.0, p0.x(), EPS);
        assertEquals(2.0, p0.y(), EPS);
        assertEquals(3.0, p0.z(), EPS);

        Point3 p90 = result.pointAt(Math.PI / 2.0, 0.0);
        assertEquals(1.0, p90.x(), 1e-9);
        assertEquals(7.0, p90.y(), 1e-9);
        assertEquals(3.0, p90.z(), 1e-9);

        Point3 north = result.pointAt(0.0, Math.PI / 2.0);
        assertEquals(1.0, north.x(), 1e-9);
        assertEquals(2.0, north.y(), 1e-9);
        assertEquals(8.0, north.z(), 1e-9);

        Vec3 n = result.normalAt(0.0, 0.0);
        assertEquals(1.0, n.x(), EPS);
        assertEquals(0.0, n.y(), EPS);
        assertEquals(0.0, n.z(), EPS);
    }

    @Test
    void evaluateSphericalSurface_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint(
                "#10",
                "('O',(0.0,0.0,0.0))",
                "O",
                List.of(0.0, 0.0, 0.0)
        );
        Axis2Placement3D placement = new Axis2Placement3D(
                "#13",
                "('A',#10,$,$)",
                "A",
                "#10",
                "$",
                "$"
        );
        SphericalSurface sphericalSurface = new SphericalSurface(
                "#20",
                "('S',#13,3.0)",
                "S",
                "#13",
                3.0
        );

        model.addEntity(origin);
        model.addEntity(placement);
        model.addEntity(sphericalSurface);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        SphericalSurface3 first = surfaceEvaluator.evaluateSphericalSurface(sphericalSurface);
        SphericalSurface3 second = surfaceEvaluator.evaluateSphericalSurface(sphericalSurface);

        assertSame(first, second);
    }

    @Test
    void evaluateToroidalSurface_shouldCreateToroidalSurface3() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint(
                "#10",
                "('O',(1.0,2.0,3.0))",
                "O",
                List.of(1.0, 2.0, 3.0)
        );
        Direction axis = new Direction(
                "#11",
                "('Z',(0.0,0.0,1.0))",
                "Z",
                List.of(0.0, 0.0, 1.0)
        );
        Direction refDirection = new Direction(
                "#12",
                "('X',(1.0,0.0,0.0))",
                "X",
                List.of(1.0, 0.0, 0.0)
        );
        Axis2Placement3D placement = new Axis2Placement3D(
                "#13",
                "('A',#10,#11,#12)",
                "A",
                "#10",
                "#11",
                "#12"
        );
        ToroidalSurface toroidalSurface = new ToroidalSurface(
                "#20",
                "('T',#13,10.0,2.0)",
                "T",
                "#13",
                10.0,
                2.0
        );

        model.addEntity(origin);
        model.addEntity(axis);
        model.addEntity(refDirection);
        model.addEntity(placement);
        model.addEntity(toroidalSurface);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        ToroidalSurface3 result = surfaceEvaluator.evaluateToroidalSurface(toroidalSurface);

        assertNotNull(result);
        assertEquals(10.0, result.majorRadius(), EPS);
        assertEquals(2.0, result.minorRadius(), EPS);

        Point3 p0 = result.pointAt(0.0, 0.0);
        assertEquals(13.0, p0.x(), EPS);
        assertEquals(2.0, p0.y(), EPS);
        assertEquals(3.0, p0.z(), EPS);

        Point3 p90u = result.pointAt(Math.PI / 2.0, 0.0);
        assertEquals(1.0, p90u.x(), 1e-9);
        assertEquals(14.0, p90u.y(), 1e-9);
        assertEquals(3.0, p90u.z(), 1e-9);

        Point3 p90v = result.pointAt(0.0, Math.PI / 2.0);
        assertEquals(11.0, p90v.x(), 1e-9);
        assertEquals(2.0, p90v.y(), 1e-9);
        assertEquals(5.0, p90v.z(), 1e-9);

        Vec3 n = result.normalAt(0.0, 0.0);
        assertEquals(1.0, n.x(), EPS);
        assertEquals(0.0, n.y(), EPS);
        assertEquals(0.0, n.z(), EPS);
    }

    @Test
    void evaluateToroidalSurface_shouldUseCache() {
        StepModel model = new StepModel();

        CartesianPoint origin = new CartesianPoint(
                "#10",
                "('O',(0.0,0.0,0.0))",
                "O",
                List.of(0.0, 0.0, 0.0)
        );
        Axis2Placement3D placement = new Axis2Placement3D(
                "#13",
                "('A',#10,$,$)",
                "A",
                "#10",
                "$",
                "$"
        );
        ToroidalSurface toroidalSurface = new ToroidalSurface(
                "#20",
                "('T',#13,8.0,2.0)",
                "T",
                "#13",
                8.0,
                2.0
        );

        model.addEntity(origin);
        model.addEntity(placement);
        model.addEntity(toroidalSurface);
        model.resolveAll();

        StepEvaluationContext context = new StepEvaluationContext(model);
        PlacementEvaluator placementEvaluator = new DefaultPlacementEvaluator(context);
        DefaultSurfaceEvaluator surfaceEvaluator = new DefaultSurfaceEvaluator(context, placementEvaluator);

        ToroidalSurface3 first = surfaceEvaluator.evaluateToroidalSurface(toroidalSurface);
        ToroidalSurface3 second = surfaceEvaluator.evaluateToroidalSurface(toroidalSurface);

        assertSame(first, second);
    }



}