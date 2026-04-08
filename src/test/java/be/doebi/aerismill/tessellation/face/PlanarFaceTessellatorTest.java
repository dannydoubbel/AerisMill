package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.surface.Surface3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.*;
import be.doebi.aerismill.tessellation.projection.DefaultPlaneProjector;
import be.doebi.aerismill.tessellation.projection.PlaneProjector;
import be.doebi.aerismill.tessellation.projection.RecordingPlaneProjector;
import be.doebi.aerismill.tessellation.face.PlanarFaceTessellator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlanarFaceTessellatorTest {
    @Test
    void tessellate_faceWithoutBounds_throws() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        FaceGeom face = new FaceGeom(
                "#1",
                new PlaneSurface3(null),
                List.of(),
                true
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(ex.getMessage().contains("face must have at least one bound"));
    }

    @Test
    void tessellate_nonPlanarFace_throwsIllegalArgumentException() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                new DefaultPlaneProjector(),
                GeometryTolerance.defaults()
        );

        FaceGeom face = new FaceGeom(
                "#2",
                createNonPlanarSurface(),
                List.of(),
                true
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(exception.getMessage().contains("only planar faces are supported for now"));
    }

    @Test
    void tessellate_planarFaceWithoutBounds_throwsIllegalArgumentException() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        FaceGeom face = new FaceGeom(
                "#1",
                new PlaneSurface3(null),
                List.of(),
                true
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(exception.getMessage().contains("face must have at least one bound"));
    }


    private Surface3 createPlanarSurface() {
        return null; // replace with your actual PlaneSurface3 constructor
    }

    private Surface3 createNonPlanarSurface() {
        return null; // replace with your actual non-planar surface constructor
    }


    private EdgeDiscretizer stubEdgeDiscretizer() {
        return (edge, tolerance) -> List.of();
    }

    private PolygonTriangulator stubPolygonTriangulator() {
        return outerLoop -> List.of();
    }

    private PlaneProjector stubPlaneProjector() {
        return new PlaneProjector() {
            @Override
            public Point2 project(Point3 point, PlaneSurface3 plane) {
                return new Point2(0.0, 0.0);
            }

            @Override
            public PolygonLoop2 projectLoop(
                    LoopGeom loop,
                    PlaneSurface3 plane,
                    EdgeDiscretizer edgeDiscretizer,
                    GeometryTolerance tolerance
            ) {
                return new PolygonLoop2(List.of());
            }
        };
    }

    @Test
    void tessellate_planarFaceWithSingleEmptyBound_throws() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        LoopGeom loop = new LoopGeom(
                "#loop1",
                List.of()
        );

        FaceGeom face = new FaceGeom(
                "#1",
                new PlaneSurface3(null),
                List.of(loop),
                true
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(ex.getMessage().contains("face bound must contain at least one edge"));
    }



    @Test
    void tessellate_planarFaceWithSingleEmptyBound_throwsIllegalArgumentException() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        LoopGeom loop = new LoopGeom(
                "#loop1",
                List.of()
        );

        FaceGeom face = new FaceGeom(
                "#1",
                new PlaneSurface3(null),
                List.of(loop),
                true
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(exception.getMessage().contains("face bound must contain at least one edge"));
    }


    @Test
    void tessellate_planarFaceWithBoundContainingOnlyNullEdges_throwsIllegalArgumentException() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        LoopGeom loop = new LoopGeom(
                "#loop1",
                java.util.Collections.singletonList(null)
        );

        FaceGeom face = new FaceGeom(
                "#1",
                new PlaneSurface3(null),
                List.of(loop),
                true
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(exception.getMessage().contains("face bound must contain at least one non-null edge"));
    }

    @Test
    void collectDiscretizedEdgePoints_shouldDiscretizeAllNonNullEdgesOfFirstBound() {
        OrientedEdgeGeom firstEdge = new OrientedEdgeGeom("#oe1", null, true);
        OrientedEdgeGeom secondEdge = new OrientedEdgeGeom("#oe2", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(firstEdge, secondEdge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        GeometryTolerance tolerance = null; // replace later if useful

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                null,
                null,
                tolerance
        );

        List<List<Point3>> result = tessellator.collectDiscretizedEdgePoints(firstBound, face, 0);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(List.of(firstEdge, secondEdge), edgeDiscretizer.recordedEdges());
        assertEquals(2, edgeDiscretizer.recordedTolerances().size());
        assertSame(tolerance, edgeDiscretizer.recordedTolerances().get(0));
        assertSame(tolerance, edgeDiscretizer.recordedTolerances().get(1));
    }



    @Test
    void flattenDiscretizedEdgePointLists_shouldFlattenPointListsInOrder() {
        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        List<List<Point3>> discretizedEdgePointLists = List.of(
                List.of(p1, p2),
                List.of(),
                List.of(p3)
        );

        List<Point3> result = tessellator.flattenDiscretizedEdgePointLists(discretizedEdgePointLists);

        assertEquals(List.of(p1, p2, p3), result);
    }

    private static final class RecordingEdgeDiscretizer implements EdgeDiscretizer {
        private final List<OrientedEdgeGeom> recordedEdges = new ArrayList<>();
        private final List<GeometryTolerance> recordedTolerances = new ArrayList<>();
        private final Map<OrientedEdgeGeom, List<Point3>> stubResults = new LinkedHashMap<>();

        @Override
        public List<Point3> discretize(OrientedEdgeGeom edge, GeometryTolerance tolerance) {
            recordedEdges.add(edge);
            recordedTolerances.add(tolerance);
            return stubResults.getOrDefault(edge, List.of());
        }

        void stubResult(OrientedEdgeGeom edge, List<Point3> points) {
            stubResults.put(edge, points);
        }

        List<OrientedEdgeGeom> recordedEdges() {
            return recordedEdges;
        }

        List<GeometryTolerance> recordedTolerances() {
            return recordedTolerances;
        }
    }
/*
    @Test
    void projectBoundaryPointsTo2D_shouldProjectAllBoundaryPointsInOrder() {
        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);

        Point2 q1 = new Point2(10.0, 20.0);
        Point2 q2 = new Point2(30.0, 40.0);

        PlaneSurface3 plane = new PlaneSurface3(null);

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                planeProjector,
                null
        );

        java.util.List<Point2> result = tessellator.projectBoundaryPointsTo2D(
                plane,
                java.util.List.of(p1, p2)
        );

        assertEquals(java.util.List.of(q1, q2), result);
        assertEquals(java.util.List.of(p1, p2), planeProjector.recordedPoints());
        assertEquals(2, planeProjector.recordedPlanes().size());
        assertSame(plane, planeProjector.recordedPlanes().get(0));
        assertSame(plane, planeProjector.recordedPlanes().get(1));
    }

 */

    @Test
    void buildOuterPolygonLoop_shouldWrapProjectedBoundaryPointsInOrder() {
        Point2 p1 = new Point2(1.0, 2.0);
        Point2 p2 = new Point2(3.0, 4.0);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonLoop2 result = tessellator.buildOuterPolygonLoop(List.of(p1, p2));

        assertNotNull(result);
        assertEquals(List.of(p1, p2), result.points());
    }

    @Test
    void buildPolygonWithNoHoles_shouldWrapOuterLoopWithEmptyHoles() {
        Point2 p1 = new Point2(1.0, 2.0);
        Point2 p2 = new Point2(3.0, 4.0);

        PolygonLoop2 outerLoop = new PolygonLoop2(List.of(p1, p2));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonWithHoles2 result = tessellator.buildPolygonWithNoHoles(outerLoop);

        assertNotNull(result);
        assertSame(outerLoop, result.outer());
        assertNotNull(result.holes());
        assertTrue(result.holes().isEmpty());
    }

    @Test
    void triangulatePolygon_shouldDelegateToPolygonTriangulator() {
        Point2 p1 = new Point2(1.0, 2.0);
        Point2 p2 = new Point2(3.0, 4.0);

        PolygonLoop2 outerLoop = new PolygonLoop2(List.of(p1, p2));
        PolygonWithHoles2 polygon = new PolygonWithHoles2(outerLoop, List.of());

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        List<int[]> expected = List.of(new int[]{0, 1, 2});
        polygonTriangulator.stubResult(expected);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                polygonTriangulator,
                null,
                null
        );

        List<int[]> result = tessellator.triangulatePolygon(polygon);

        assertSame(polygon, polygonTriangulator.recordedPolygon());
        assertSame(expected, result);
    }

    @Test
    void tessellate_shouldTriangulateBuiltPolygon_andStillReturnEmptyPatch() {
        OrientedEdgeGeom edge = new OrientedEdgeGeom("#oe1", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(edge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);


        Point2 q1 = new Point2(0.0, 0.0);
        Point2 q2 = new Point2(1.0, 0.0);
        Point2 q3 = new Point2(0.0, 1.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(edge, List.of(p1, p2, p3));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        List<int[]> triangulationResult = List.of(new int[]{0, 1, 2});
        polygonTriangulator.stubResult(triangulationResult);

        GeometryTolerance tolerance = null;

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                tolerance
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertNotNull(result.vertices());
        assertNotNull(result.triangles());

        assertEquals(List.of(p1, p2, p3), result.vertices());
        assertEquals(1, result.triangles().size());
        assertSame(triangulationResult.getFirst(), result.triangles().getFirst());

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(q1, q2, q3), polygonTriangulator.recordedPolygon().outer().points());
        assertNotNull(polygonTriangulator.recordedPolygon().holes());
        assertTrue(polygonTriangulator.recordedPolygon().holes().isEmpty());

        assertEquals(List.of(edge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(p1, p2, p3), planeProjector.recordedPoints());
    }

    @Test
    void buildFaceMeshPatch_shouldWrapVerticesAndTriangles() {
        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        int[] triangle = new int[]{0, 1, 2};

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        FaceMeshPatch result = tessellator.buildFaceMeshPatch(
                List.of(p1, p2, p3),
                List.of(triangle)
        );

        assertNotNull(result);
        assertEquals(List.of(p1, p2, p3), result.vertices());
        assertEquals(1, result.triangles().size());
        assertSame(triangle, result.triangles().get(0));
    }

    @Test
    void tessellate_shouldTriangulateBuiltPolygon_andReturnMeshPatch() {
        OrientedEdgeGeom edge = new OrientedEdgeGeom("#oe1", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(edge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        Point2 q1 = new Point2(0.0, 0.0);
        Point2 q2 = new Point2(1.0, 0.0);
        Point2 q3 = new Point2(0.0, 1.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(edge, List.of(p1, p2, p3));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] triangle = new int[]{0, 1, 2};
        polygonTriangulator.stubResult(List.of(triangle));

        GeometryTolerance tolerance = null;

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                tolerance
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(List.of(p1, p2, p3), result.vertices());
        assertEquals(1, result.triangles().size());
        assertSame(triangle, result.triangles().get(0));

        assertEquals(List.of(edge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(p1, p2, p3), planeProjector.recordedPoints());

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(q1, q2, q3), polygonTriangulator.recordedPolygon().outer().points());
        assertNotNull(polygonTriangulator.recordedPolygon().holes());
        assertTrue(polygonTriangulator.recordedPolygon().holes().isEmpty());
    }

    @Test
    void collapseConsecutiveDuplicateBoundaryPoints_shouldRemoveAdjacentDuplicates_andPreserveOrder() {
        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);
        Point3 p4 = new Point3(4.0, 0.0, 0.0);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        List<Point3> result = tessellator.collapseConsecutiveDuplicateBoundaryPoints(
                List.of(p1, p2, p2, p3, p3, p4)
        );

        assertEquals(List.of(p1, p2, p3, p4), result);
    }

    @Test
    void collapseConsecutiveDuplicateBoundaryPoints_shouldKeepNonConsecutiveDuplicates() {
        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        List<Point3> result = tessellator.collapseConsecutiveDuplicateBoundaryPoints(
                List.of(p1, p2, p1, p3)
        );

        assertEquals(List.of(p1, p2, p1, p3), result);
    }

    @Test
    void tessellate_shouldCollapseSharedSeamPointBetweenDiscretizedEdges_beforeBuildingMeshPatch() {
        OrientedEdgeGeom firstEdge = new OrientedEdgeGeom("#oe1", null, true);
        OrientedEdgeGeom secondEdge = new OrientedEdgeGeom("#oe2", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(firstEdge, secondEdge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        Point2 q1 = new Point2(0.0, 0.0);
        Point2 q2 = new Point2(1.0, 0.0);
        Point2 q3 = new Point2(0.0, 1.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(firstEdge, List.of(p1, p2));
        edgeDiscretizer.stubResult(secondEdge, List.of(p2, p3));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] triangle = new int[]{0, 1, 2};
        polygonTriangulator.stubResult(List.of(triangle));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(List.of(p1, p2, p3), result.vertices());
        assertEquals(1, result.triangles().size());
        assertSame(triangle, result.triangles().get(0));

        assertEquals(List.of(firstEdge, secondEdge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(p1, p2, p3), planeProjector.recordedPoints());

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(q1, q2, q3), polygonTriangulator.recordedPolygon().outer().points());
        assertTrue(polygonTriangulator.recordedPolygon().holes().isEmpty());
    }

    @Test
    void removeClosingDuplicateBoundaryPoint_shouldRemoveLastPoint_whenItEqualsFirst() {
        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        List<Point3> result = tessellator.removeClosingDuplicateBoundaryPoint(
                List.of(p1, p2, p3, p1)
        );

        assertEquals(List.of(p1, p2, p3), result);
    }

    @Test
    void removeClosingDuplicateBoundaryPoint_shouldKeepLastPoint_whenItDiffersFromFirst() {
        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        List<Point3> result = tessellator.removeClosingDuplicateBoundaryPoint(
                List.of(p1, p2, p3)
        );

        assertEquals(List.of(p1, p2, p3), result);
    }

    @Test
    void tessellate_shouldRemoveClosingDuplicateBoundaryPoint_beforeBuildingMeshPatch() {
        OrientedEdgeGeom firstEdge = new OrientedEdgeGeom("#oe1", null, true);
        OrientedEdgeGeom secondEdge = new OrientedEdgeGeom("#oe2", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(firstEdge, secondEdge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        Point2 q1 = new Point2(0.0, 0.0);
        Point2 q2 = new Point2(1.0, 0.0);
        Point2 q3 = new Point2(0.0, 1.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(firstEdge, List.of(p1, p2));
        edgeDiscretizer.stubResult(secondEdge, List.of(p3, p1));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] triangle = new int[]{0, 1, 2};
        polygonTriangulator.stubResult(List.of(triangle));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(List.of(p1, p2, p3), result.vertices());
        assertEquals(1, result.triangles().size());
        assertSame(triangle, result.triangles().get(0));

        assertEquals(List.of(firstEdge, secondEdge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(p1, p2, p3), planeProjector.recordedPoints());

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(q1, q2, q3), polygonTriangulator.recordedPolygon().outer().points());
        assertTrue(polygonTriangulator.recordedPolygon().holes().isEmpty());
    }


    @Test
    void validateBoundaryHasAtLeastThreePoints_shouldNotThrow_whenBoundaryHasThreePoints() {
        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        FaceGeom face = planarFace("#face1", loop("#loop1"));

        assertDoesNotThrow(() ->
                tessellator.validateBoundaryHasAtLeastThreePoints(
                        List.of(p1, p2, p3),
                        face,
                        0,
                        3,
                        3
                )
        );
    }

    @Test
    void tessellate_shouldThrow_whenBoundaryCleanupLeavesFewerThanThreePoints() {
        OrientedEdgeGeom firstEdge = new OrientedEdgeGeom("#oe1", null, true);
        OrientedEdgeGeom secondEdge = new OrientedEdgeGeom("#oe2", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(firstEdge, secondEdge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(firstEdge, List.of(p1, p2));
        edgeDiscretizer.stubResult(secondEdge, List.of(p2, p1));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(ex.getMessage().contains("boundary has only 2 point(s) after cleanup; at least 3 required for triangulation"));

        assertEquals(List.of(firstEdge, secondEdge), edgeDiscretizer.recordedEdges());
        assertTrue(planeProjector.recordedPoints().isEmpty());
        assertNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_shouldThrow_whenTriangulationProducesNoTriangles() {
        OrientedEdgeGeom edge = new OrientedEdgeGeom("#oe1", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(edge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        Point2 q1 = new Point2(10.0, 20.0);
        Point2 q2 = new Point2(30.0, 40.0);
        Point2 q3 = new Point2(50.0, 60.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(edge, List.of(p1, p2, p3));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        polygonTriangulator.stubResult(List.of());

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(ex.getMessage().contains("triangulation produced no triangles"));

        assertNotNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_shouldThrow_whenTriangulationContainsOutOfBoundsIndex() {
        OrientedEdgeGeom edge = new OrientedEdgeGeom("#oe1", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(edge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        Point2 q1 = new Point2(10.0, 20.0);
        Point2 q2 = new Point2(30.0, 40.0);
        Point2 q3 = new Point2(50.0, 60.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(edge, List.of(p1, p2, p3));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        polygonTriangulator.stubResult(List.of(new int[]{0, 1, 3}));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Triangle index out of bounds for boundary vertices.", ex.getMessage());
    }

    @Test
    void tessellate_shouldThrow_whenTriangulationContainsDuplicateVertexIndices() {
        OrientedEdgeGeom edge = new OrientedEdgeGeom("#oe1", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(edge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        Point2 q1 = new Point2(10.0, 20.0);
        Point2 q2 = new Point2(30.0, 40.0);
        Point2 q3 = new Point2(50.0, 60.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(edge, List.of(p1, p2, p3));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        polygonTriangulator.stubResult(List.of(new int[]{0, 1, 1}));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Triangle must reference three distinct vertex indices.", ex.getMessage());

        assertEquals(List.of(edge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(p1, p2, p3), planeProjector.recordedPoints());
        assertNotNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_shouldThrow_whenTriangulationProducesZeroAreaTriangleInProjectedSpace() {
        OrientedEdgeGeom edge = new OrientedEdgeGeom("#oe1", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(edge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        Point2 q1 = new Point2(0.0, 0.0);
        Point2 q2 = new Point2(1.0, 1.0);
        Point2 q3 = new Point2(2.0, 2.0); // collinear with q1 and q2

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(edge, List.of(p1, p2, p3));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        polygonTriangulator.stubResult(List.of(new int[]{0, 1, 2}));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Triangle must have non-zero area in projected space.", ex.getMessage());

        assertEquals(List.of(edge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(p1, p2, p3), planeProjector.recordedPoints());
        assertNotNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_shouldReturnMeshPatchWithMultipleTriangles_forValidQuadBoundary() {
        OrientedEdgeGeom edge = new OrientedEdgeGeom("#oe1", null, true);

        LoopGeom firstBound = new LoopGeom("#l1", List.of(edge));
        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(firstBound),
                true
        );

        Point3 p1 = new Point3(0.0, 0.0, 0.0);
        Point3 p2 = new Point3(1.0, 0.0, 0.0);
        Point3 p3 = new Point3(1.0, 1.0, 0.0);
        Point3 p4 = new Point3(0.0, 1.0, 0.0);

        Point2 q1 = new Point2(0.0, 0.0);
        Point2 q2 = new Point2(1.0, 0.0);
        Point2 q3 = new Point2(1.0, 1.0);
        Point2 q4 = new Point2(0.0, 1.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(edge, List.of(p1, p2, p3, p4));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);
        planeProjector.stubResult(p4, q4);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] t1 = new int[]{0, 1, 2};
        int[] t2 = new int[]{0, 2, 3};
        polygonTriangulator.stubResult(List.of(t1, t2));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(List.of(p1, p2, p3, p4), result.vertices());
        assertEquals(2, result.triangles().size());
        assertSame(t1, result.triangles().get(0));
        assertSame(t2, result.triangles().get(1));

        assertEquals(List.of(edge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(p1, p2, p3, p4), planeProjector.recordedPoints());

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(q1, q2, q3, q4), polygonTriangulator.recordedPolygon().outer().points());
        assertTrue(polygonTriangulator.recordedPolygon().holes().isEmpty());
    }

    @Test
    void validateProjectedBoundaryIsSimple_shouldThrow_whenBoundarySelfIntersects() {
        Point2 q1 = new Point2(0.0, 0.0);
        Point2 q2 = new Point2(2.0, 2.0);
        Point2 q3 = new Point2(0.0, 2.0);
        Point2 q4 = new Point2(2.0, 0.0);

        PolygonLoop2 outerLoop = new PolygonLoop2(List.of(q1, q2, q3, q4));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        FaceGeom face = planarFace("#face1", loop("#loop1"));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateProjectedBoundaryIsSimple(outerLoop, face, 0)
        );

        assertTrue(ex.getMessage().contains("projected boundary must not self-intersect"));
    }

    @Test
    void validateProjectedBoundaryIsSimple_shouldNotThrow_whenBoundaryIsSimple() {
        Point2 q1 = new Point2(0.0, 0.0);
        Point2 q2 = new Point2(2.0, 0.0);
        Point2 q3 = new Point2(2.0, 2.0);
        Point2 q4 = new Point2(0.0, 2.0);

        PolygonLoop2 outerLoop = new PolygonLoop2(List.of(q1, q2, q3, q4));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );
        FaceGeom face = planarFace("#face1", loop("#loop1"));
        assertDoesNotThrow(() -> tessellator.validateProjectedBoundaryIsSimple(outerLoop,face,0));
    }

    @Test
    void prepareProjectedPolygonLoop_shouldReturnCleanProjectedLoop_forValidBound() {
        OrientedEdgeGeom firstEdge = new OrientedEdgeGeom("#oe1", null, true);
        OrientedEdgeGeom secondEdge = new OrientedEdgeGeom("#oe2", null, true);

        LoopGeom loop = new LoopGeom("#l1", List.of(firstEdge, secondEdge));
        PlaneSurface3 plane = new PlaneSurface3(null);

        Point3 p1 = new Point3(1.0, 0.0, 0.0);
        Point3 p2 = new Point3(2.0, 0.0, 0.0);
        Point3 p3 = new Point3(3.0, 0.0, 0.0);

        Point2 q1 = new Point2(10.0, 20.0);
        Point2 q2 = new Point2(30.0, 40.0);
        Point2 q3 = new Point2(50.0, 60.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(firstEdge, List.of(p1, p2));
        edgeDiscretizer.stubResult(secondEdge, List.of(p2, p3, p1));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(p1, q1);
        planeProjector.stubResult(p2, q2);
        planeProjector.stubResult(p3, q3);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                null,
                planeProjector,
                null
        );

        FaceGeom face = planarFace("#face1", loop);

        var result = tessellator.prepareProjectedPolygonLoop(face, loop, plane, 0);

        assertEquals(List.of(p1, p2, p3), result.boundaryPoints());
        assertNotNull(result.polygonLoop());
        assertEquals(List.of(q1, q2, q3), result.polygonLoop().points());

        assertEquals(List.of(firstEdge, secondEdge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(p1, p2, p3), planeProjector.recordedPoints());
    }

    @Test
    void tessellate_planarFaceWithOuterAndHole_buildsPolygonWithHoles_andReturnsMeshPatch() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge = new OrientedEdgeGeom("#oeHole", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, holeBound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(4.0, 0.0, 0.0);
        Point3 o3 = new Point3(4.0, 4.0, 0.0);
        Point3 o4 = new Point3(0.0, 4.0, 0.0);

        Point3 h1 = new Point3(1.0, 1.0, 0.0);
        Point3 h2 = new Point3(2.0, 1.0, 0.0);
        Point3 h3 = new Point3(2.0, 2.0, 0.0);
        Point3 h4 = new Point3(1.0, 2.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(4.0, 0.0);
        Point2 qO3 = new Point2(4.0, 4.0);
        Point2 qO4 = new Point2(0.0, 4.0);

        Point2 qH1 = new Point2(1.0, 1.0);
        Point2 qH2 = new Point2(2.0, 1.0);
        Point2 qH3 = new Point2(2.0, 2.0);
        Point2 qH4 = new Point2(1.0, 2.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge, List.of(h1, h2, h3, h4));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h1, qH1);
        planeProjector.stubResult(h2, qH2);
        planeProjector.stubResult(h3, qH3);
        planeProjector.stubResult(h4, qH4);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] t1 = new int[]{0, 1, 2};
        int[] t2 = new int[]{4, 5, 6};
        polygonTriangulator.stubResult(List.of(t1, t2));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), result.vertices());
        assertEquals(2, result.triangles().size());
        assertSame(t1, result.triangles().get(0));
        assertSame(t2, result.triangles().get(1));

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(qO1, qO2, qO3, qO4), polygonTriangulator.recordedPolygon().outer().points());
        assertEquals(1, polygonTriangulator.recordedPolygon().holes().size());
        assertEquals(List.of(qH1, qH2, qH3, qH4), polygonTriangulator.recordedPolygon().holes().getFirst().points());
    }

    @Test
    void prepareProjectedPolygonLoops_shouldThrow_whenHoleCleanupLeavesFewerThanThreePoints() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge1 = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom holeEdge2 = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge1, holeEdge2));

        PlaneSurface3 plane = new PlaneSurface3(null);

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(4.0, 0.0, 0.0);
        Point3 o3 = new Point3(4.0, 4.0, 0.0);
        Point3 o4 = new Point3(0.0, 4.0, 0.0);

        Point3 h1 = new Point3(1.0, 1.0, 0.0);
        Point3 h2 = new Point3(2.0, 1.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(4.0, 0.0);
        Point2 qO3 = new Point2(4.0, 4.0);
        Point2 qO4 = new Point2(0.0, 4.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge1, List.of(h1, h2));
        edgeDiscretizer.stubResult(holeEdge2, List.of(h2, h1));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                null,
                planeProjector,
                null
        );

        FaceGeom face = planarFace("#face1", outerBound, holeBound);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.prepareProjectedPolygonLoops(face, plane)
        );

        assertTrue(ex.getMessage().contains("boundary has only 2 point(s) after cleanup; at least 3 required for triangulation"));

        assertEquals(List.of(outerEdge, holeEdge1, holeEdge2), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4), planeProjector.recordedPoints());
    }

    @Test
    void tessellate_shouldThrow_whenHoleCleanupLeavesFewerThanThreePoints() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge1 = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom holeEdge2 = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge1, holeEdge2));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, holeBound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(4.0, 0.0, 0.0);
        Point3 o3 = new Point3(4.0, 4.0, 0.0);
        Point3 o4 = new Point3(0.0, 4.0, 0.0);

        Point3 h1 = new Point3(1.0, 1.0, 0.0);
        Point3 h2 = new Point3(2.0, 1.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(4.0, 0.0);
        Point2 qO3 = new Point2(4.0, 4.0);
        Point2 qO4 = new Point2(0.0, 4.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge1, List.of(h1, h2));
        edgeDiscretizer.stubResult(holeEdge2, List.of(h2, h1));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(ex.getMessage().contains("boundary has only 2 point(s) after cleanup; at least 3 required for triangulation"));

        assertEquals(List.of(outerEdge, holeEdge1, holeEdge2), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4), planeProjector.recordedPoints());
        assertNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_shouldThrow_whenProjectedHoleSelfIntersects() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge = new OrientedEdgeGeom("#oeHole", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, holeBound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(4.0, 0.0, 0.0);
        Point3 o3 = new Point3(4.0, 4.0, 0.0);
        Point3 o4 = new Point3(0.0, 4.0, 0.0);

        Point3 h1 = new Point3(1.0, 1.0, 0.0);
        Point3 h2 = new Point3(2.0, 2.0, 0.0);
        Point3 h3 = new Point3(1.0, 2.0, 0.0);
        Point3 h4 = new Point3(2.0, 1.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(4.0, 0.0);
        Point2 qO3 = new Point2(4.0, 4.0);
        Point2 qO4 = new Point2(0.0, 4.0);

        Point2 qH1 = new Point2(1.0, 1.0);
        Point2 qH2 = new Point2(2.0, 2.0);
        Point2 qH3 = new Point2(1.0, 2.0);
        Point2 qH4 = new Point2(2.0, 1.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge, List.of(h1, h2, h3, h4));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h1, qH1);
        planeProjector.stubResult(h2, qH2);
        planeProjector.stubResult(h3, qH3);
        planeProjector.stubResult(h4, qH4);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );


        assertTrue(ex.getMessage().contains("projected boundary must not self-intersect"));

        assertEquals(List.of(outerEdge, holeEdge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), planeProjector.recordedPoints());
        assertNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void prepareProjectedPolygonLoops_shouldThrow_whenProjectedHoleSelfIntersects() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge = new OrientedEdgeGeom("#oeHole", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge));

        PlaneSurface3 plane = new PlaneSurface3(null);

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(4.0, 0.0, 0.0);
        Point3 o3 = new Point3(4.0, 4.0, 0.0);
        Point3 o4 = new Point3(0.0, 4.0, 0.0);

        Point3 h1 = new Point3(1.0, 1.0, 0.0);
        Point3 h2 = new Point3(2.0, 2.0, 0.0);
        Point3 h3 = new Point3(1.0, 2.0, 0.0);
        Point3 h4 = new Point3(2.0, 1.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(4.0, 0.0);
        Point2 qO3 = new Point2(4.0, 4.0);
        Point2 qO4 = new Point2(0.0, 4.0);

        Point2 qH1 = new Point2(1.0, 1.0);
        Point2 qH2 = new Point2(2.0, 2.0);
        Point2 qH3 = new Point2(1.0, 2.0);
        Point2 qH4 = new Point2(2.0, 1.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge, List.of(h1, h2, h3, h4));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h1, qH1);
        planeProjector.stubResult(h2, qH2);
        planeProjector.stubResult(h3, qH3);
        planeProjector.stubResult(h4, qH4);

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                null,
                planeProjector,
                null
        );
        FaceGeom face = planarFace("#face1", outerBound, holeBound);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.prepareProjectedPolygonLoops(face, plane)
        );


        assertTrue(ex.getMessage().contains("projected boundary must not self-intersect"));

        assertEquals(List.of(outerEdge, holeEdge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), planeProjector.recordedPoints());
    }

    @Test
    void tessellate_planarFaceWithOuterAndTwoHoles_buildsPolygonWithHoles_andReturnsMeshPatch() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom hole1Edge = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom hole2Edge = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom hole1Bound = new LoopGeom("#hole1", List.of(hole1Edge));
        LoopGeom hole2Bound = new LoopGeom("#hole2", List.of(hole2Edge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, hole1Bound, hole2Bound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(6.0, 0.0, 0.0);
        Point3 o3 = new Point3(6.0, 6.0, 0.0);
        Point3 o4 = new Point3(0.0, 6.0, 0.0);

        Point3 h11 = new Point3(1.0, 1.0, 0.0);
        Point3 h12 = new Point3(2.0, 1.0, 0.0);
        Point3 h13 = new Point3(2.0, 2.0, 0.0);
        Point3 h14 = new Point3(1.0, 2.0, 0.0);

        Point3 h21 = new Point3(4.0, 4.0, 0.0);
        Point3 h22 = new Point3(5.0, 4.0, 0.0);
        Point3 h23 = new Point3(5.0, 5.0, 0.0);
        Point3 h24 = new Point3(4.0, 5.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(6.0, 0.0);
        Point2 qO3 = new Point2(6.0, 6.0);
        Point2 qO4 = new Point2(0.0, 6.0);

        Point2 qH11 = new Point2(1.0, 1.0);
        Point2 qH12 = new Point2(2.0, 1.0);
        Point2 qH13 = new Point2(2.0, 2.0);
        Point2 qH14 = new Point2(1.0, 2.0);

        Point2 qH21 = new Point2(4.0, 4.0);
        Point2 qH22 = new Point2(5.0, 4.0);
        Point2 qH23 = new Point2(5.0, 5.0);
        Point2 qH24 = new Point2(4.0, 5.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(hole1Edge, List.of(h11, h12, h13, h14));
        edgeDiscretizer.stubResult(hole2Edge, List.of(h21, h22, h23, h24));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);

        planeProjector.stubResult(h11, qH11);
        planeProjector.stubResult(h12, qH12);
        planeProjector.stubResult(h13, qH13);
        planeProjector.stubResult(h14, qH14);

        planeProjector.stubResult(h21, qH21);
        planeProjector.stubResult(h22, qH22);
        planeProjector.stubResult(h23, qH23);
        planeProjector.stubResult(h24, qH24);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] t1 = new int[]{0, 1, 2};
        int[] t2 = new int[]{4, 5, 6};
        int[] t3 = new int[]{8, 9, 10};
        polygonTriangulator.stubResult(List.of(t1, t2, t3));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24),
                result.vertices()
        );
        assertEquals(3, result.triangles().size());
        assertSame(t1, result.triangles().get(0));
        assertSame(t2, result.triangles().get(1));
        assertSame(t3, result.triangles().get(2));

        assertEquals(List.of(outerEdge, hole1Edge, hole2Edge), edgeDiscretizer.recordedEdges());
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24),
                planeProjector.recordedPoints()
        );

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(qO1, qO2, qO3, qO4), polygonTriangulator.recordedPolygon().outer().points());
        assertEquals(2, polygonTriangulator.recordedPolygon().holes().size());
        assertEquals(List.of(qH11, qH12, qH13, qH14), polygonTriangulator.recordedPolygon().holes().get(0).points());
        assertEquals(List.of(qH21, qH22, qH23, qH24), polygonTriangulator.recordedPolygon().holes().get(1).points());
    }

    @Test
    void buildPolygonWithHoles_shouldUseFirstPreparedLoopAsOuter_andRemainingAsHoles() {
        PolygonLoop2 outer = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(1.0, 0.0),
                new Point2(1.0, 1.0)
        ));

        PolygonLoop2 hole1 = new PolygonLoop2(List.of(
                new Point2(0.2, 0.2),
                new Point2(0.3, 0.2),
                new Point2(0.3, 0.3)
        ));

        PolygonLoop2 hole2 = new PolygonLoop2(List.of(
                new Point2(0.6, 0.6),
                new Point2(0.7, 0.6),
                new Point2(0.7, 0.7)
        ));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        var preparedLoops = List.of(
                new PlanarFaceTessellator.PreparedLoop(List.of(), outer),
                new PlanarFaceTessellator.PreparedLoop(List.of(), hole1),
                new PlanarFaceTessellator.PreparedLoop(List.of(), hole2)
        );

        PolygonWithHoles2 result = tessellator.buildPolygonWithHoles(preparedLoops);

        assertSame(outer, result.outer());
        assertEquals(2, result.holes().size());
        assertSame(hole1, result.holes().get(0));
        assertSame(hole2, result.holes().get(1));
    }

    @Test
    void tessellate_planarFaceWithOuterAndTwoHoles_allowsTriangleIndicesFromSecondHole() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom hole1Edge = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom hole2Edge = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom hole1Bound = new LoopGeom("#hole1", List.of(hole1Edge));
        LoopGeom hole2Bound = new LoopGeom("#hole2", List.of(hole2Edge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, hole1Bound, hole2Bound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(6.0, 0.0, 0.0);
        Point3 o3 = new Point3(6.0, 6.0, 0.0);
        Point3 o4 = new Point3(0.0, 6.0, 0.0);

        Point3 h11 = new Point3(1.0, 1.0, 0.0);
        Point3 h12 = new Point3(2.0, 1.0, 0.0);
        Point3 h13 = new Point3(2.0, 2.0, 0.0);
        Point3 h14 = new Point3(1.0, 2.0, 0.0);

        Point3 h21 = new Point3(4.0, 4.0, 0.0);
        Point3 h22 = new Point3(5.0, 4.0, 0.0);
        Point3 h23 = new Point3(5.0, 5.0, 0.0);
        Point3 h24 = new Point3(4.0, 5.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(6.0, 0.0);
        Point2 qO3 = new Point2(6.0, 6.0);
        Point2 qO4 = new Point2(0.0, 6.0);

        Point2 qH11 = new Point2(1.0, 1.0);
        Point2 qH12 = new Point2(2.0, 1.0);
        Point2 qH13 = new Point2(2.0, 2.0);
        Point2 qH14 = new Point2(1.0, 2.0);

        Point2 qH21 = new Point2(4.0, 4.0);
        Point2 qH22 = new Point2(5.0, 4.0);
        Point2 qH23 = new Point2(5.0, 5.0);
        Point2 qH24 = new Point2(4.0, 5.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(hole1Edge, List.of(h11, h12, h13, h14));
        edgeDiscretizer.stubResult(hole2Edge, List.of(h21, h22, h23, h24));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h11, qH11);
        planeProjector.stubResult(h12, qH12);
        planeProjector.stubResult(h13, qH13);
        planeProjector.stubResult(h14, qH14);
        planeProjector.stubResult(h21, qH21);
        planeProjector.stubResult(h22, qH22);
        planeProjector.stubResult(h23, qH23);
        planeProjector.stubResult(h24, qH24);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] triangle = new int[]{8, 9, 10};
        polygonTriangulator.stubResult(List.of(triangle));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24),
                result.vertices()
        );
        assertEquals(1, result.triangles().size());
        assertSame(triangle, result.triangles().get(0));

        assertEquals(List.of(outerEdge, hole1Edge, hole2Edge), edgeDiscretizer.recordedEdges());
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24),
                planeProjector.recordedPoints()
        );

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(qO1, qO2, qO3, qO4), polygonTriangulator.recordedPolygon().outer().points());
        assertEquals(2, polygonTriangulator.recordedPolygon().holes().size());
        assertEquals(List.of(qH11, qH12, qH13, qH14), polygonTriangulator.recordedPolygon().holes().get(0).points());
        assertEquals(List.of(qH21, qH22, qH23, qH24), polygonTriangulator.recordedPolygon().holes().get(1).points());
    }

    @Test
    void tessellate_planarFaceWithOuterAndTwoHoles_throwsWhenTriangleIndexIsOutOfCombinedBounds() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom hole1Edge = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom hole2Edge = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom hole1Bound = new LoopGeom("#hole1", List.of(hole1Edge));
        LoopGeom hole2Bound = new LoopGeom("#hole2", List.of(hole2Edge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, hole1Bound, hole2Bound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(6.0, 0.0, 0.0);
        Point3 o3 = new Point3(6.0, 6.0, 0.0);
        Point3 o4 = new Point3(0.0, 6.0, 0.0);

        Point3 h11 = new Point3(1.0, 1.0, 0.0);
        Point3 h12 = new Point3(2.0, 1.0, 0.0);
        Point3 h13 = new Point3(2.0, 2.0, 0.0);
        Point3 h14 = new Point3(1.0, 2.0, 0.0);

        Point3 h21 = new Point3(4.0, 4.0, 0.0);
        Point3 h22 = new Point3(5.0, 4.0, 0.0);
        Point3 h23 = new Point3(5.0, 5.0, 0.0);
        Point3 h24 = new Point3(4.0, 5.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(6.0, 0.0);
        Point2 qO3 = new Point2(6.0, 6.0);
        Point2 qO4 = new Point2(0.0, 6.0);

        Point2 qH11 = new Point2(1.0, 1.0);
        Point2 qH12 = new Point2(2.0, 1.0);
        Point2 qH13 = new Point2(2.0, 2.0);
        Point2 qH14 = new Point2(1.0, 2.0);

        Point2 qH21 = new Point2(4.0, 4.0);
        Point2 qH22 = new Point2(5.0, 4.0);
        Point2 qH23 = new Point2(5.0, 5.0);
        Point2 qH24 = new Point2(4.0, 5.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(hole1Edge, List.of(h11, h12, h13, h14));
        edgeDiscretizer.stubResult(hole2Edge, List.of(h21, h22, h23, h24));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h11, qH11);
        planeProjector.stubResult(h12, qH12);
        planeProjector.stubResult(h13, qH13);
        planeProjector.stubResult(h14, qH14);
        planeProjector.stubResult(h21, qH21);
        planeProjector.stubResult(h22, qH22);
        planeProjector.stubResult(h23, qH23);
        planeProjector.stubResult(h24, qH24);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        polygonTriangulator.stubResult(List.of(new int[]{8, 9, 12}));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Triangle index out of bounds for boundary vertices.", ex.getMessage());

        assertEquals(List.of(outerEdge, hole1Edge, hole2Edge), edgeDiscretizer.recordedEdges());
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24),
                planeProjector.recordedPoints()
        );
        assertNotNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_planarFaceWithOuterAndTwoHoles_throwsWhenTriangleHasZeroAreaInCombinedProjectedSpace() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom hole1Edge = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom hole2Edge = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom hole1Bound = new LoopGeom("#hole1", List.of(hole1Edge));
        LoopGeom hole2Bound = new LoopGeom("#hole2", List.of(hole2Edge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, hole1Bound, hole2Bound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(6.0, 0.0, 0.0);
        Point3 o3 = new Point3(6.0, 6.0, 0.0);
        Point3 o4 = new Point3(0.0, 6.0, 0.0);

        Point3 h11 = new Point3(1.0, 1.0, 0.0);
        Point3 h12 = new Point3(2.0, 1.0, 0.0);
        Point3 h13 = new Point3(2.0, 2.0, 0.0);
        Point3 h14 = new Point3(1.0, 2.0, 0.0);

        Point3 h21 = new Point3(4.0, 4.0, 0.0);
        Point3 h22 = new Point3(4.5, 4.0, 0.0);
        Point3 h23 = new Point3(5.0, 4.0, 0.0);
        Point3 h24 = new Point3(4.5, 4.5, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(6.0, 0.0);
        Point2 qO3 = new Point2(6.0, 6.0);
        Point2 qO4 = new Point2(0.0, 6.0);

        Point2 qH11 = new Point2(1.0, 1.0);
        Point2 qH12 = new Point2(2.0, 1.0);
        Point2 qH13 = new Point2(2.0, 2.0);
        Point2 qH14 = new Point2(1.0, 2.0);

        Point2 qH21 = new Point2(4.0, 4.0);
        Point2 qH22 = new Point2(4.5, 4.0);
        Point2 qH23 = new Point2(5.0, 4.0);
        Point2 qH24 = new Point2(4.5, 4.5);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(hole1Edge, List.of(h11, h12, h13, h14));

        edgeDiscretizer.stubResult(hole2Edge, List.of(h21, h22, h23));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h11, qH11);
        planeProjector.stubResult(h12, qH12);
        planeProjector.stubResult(h13, qH13);
        planeProjector.stubResult(h14, qH14);
        planeProjector.stubResult(h21, qH21);
        planeProjector.stubResult(h22, qH22);
        planeProjector.stubResult(h23, qH23);
        planeProjector.stubResult(h24, qH24);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        polygonTriangulator.stubResult(List.of(new int[]{8, 9, 10}));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Triangle must have non-zero area in projected space.", ex.getMessage());

        assertEquals(List.of(outerEdge, hole1Edge, hole2Edge), edgeDiscretizer.recordedEdges());
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23),
                planeProjector.recordedPoints()
        );
        assertNotNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_planarFaceWithHoleSharedSeamDuplicate_collapsesHoleBoundaryBeforePolygonBuild() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge1 = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom holeEdge2 = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge1, holeEdge2));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, holeBound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(6.0, 0.0, 0.0);
        Point3 o3 = new Point3(6.0, 6.0, 0.0);
        Point3 o4 = new Point3(0.0, 6.0, 0.0);

        Point3 h1 = new Point3(1.0, 1.0, 0.0);
        Point3 h2 = new Point3(2.0, 1.0, 0.0);
        Point3 h3 = new Point3(2.0, 2.0, 0.0);
        Point3 h4 = new Point3(1.0, 2.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(6.0, 0.0);
        Point2 qO3 = new Point2(6.0, 6.0);
        Point2 qO4 = new Point2(0.0, 6.0);

        Point2 qH1 = new Point2(1.0, 1.0);
        Point2 qH2 = new Point2(2.0, 1.0);
        Point2 qH3 = new Point2(2.0, 2.0);
        Point2 qH4 = new Point2(1.0, 2.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge1, List.of(h1, h2));
        edgeDiscretizer.stubResult(holeEdge2, List.of(h2, h3, h4));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h1, qH1);
        planeProjector.stubResult(h2, qH2);
        planeProjector.stubResult(h3, qH3);
        planeProjector.stubResult(h4, qH4);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] triangle = new int[]{4, 5, 6};
        polygonTriangulator.stubResult(List.of(triangle));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), result.vertices());
        assertEquals(1, result.triangles().size());
        assertSame(triangle, result.triangles().get(0));

        assertEquals(List.of(outerEdge, holeEdge1, holeEdge2), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), planeProjector.recordedPoints());

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(qO1, qO2, qO3, qO4), polygonTriangulator.recordedPolygon().outer().points());
        assertEquals(1, polygonTriangulator.recordedPolygon().holes().size());
        assertEquals(List.of(qH1, qH2, qH3, qH4), polygonTriangulator.recordedPolygon().holes().get(0).points());
    }

    @Test
    void tessellate_planarFaceWithHoleClosingDuplicate_removesClosingPointBeforePolygonBuild() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge = new OrientedEdgeGeom("#oeHole", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, holeBound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(6.0, 0.0, 0.0);
        Point3 o3 = new Point3(6.0, 6.0, 0.0);
        Point3 o4 = new Point3(0.0, 6.0, 0.0);

        Point3 h1 = new Point3(1.0, 1.0, 0.0);
        Point3 h2 = new Point3(2.0, 1.0, 0.0);
        Point3 h3 = new Point3(2.0, 2.0, 0.0);
        Point3 h4 = new Point3(1.0, 2.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(6.0, 0.0);
        Point2 qO3 = new Point2(6.0, 6.0);
        Point2 qO4 = new Point2(0.0, 6.0);

        Point2 qH1 = new Point2(1.0, 1.0);
        Point2 qH2 = new Point2(2.0, 1.0);
        Point2 qH3 = new Point2(2.0, 2.0);
        Point2 qH4 = new Point2(1.0, 2.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge, List.of(h1, h2, h3, h4, h1));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h1, qH1);
        planeProjector.stubResult(h2, qH2);
        planeProjector.stubResult(h3, qH3);
        planeProjector.stubResult(h4, qH4);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] triangle = new int[]{4, 5, 6};
        polygonTriangulator.stubResult(List.of(triangle));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), result.vertices());
        assertEquals(1, result.triangles().size());
        assertSame(triangle, result.triangles().get(0));

        assertEquals(List.of(outerEdge, holeEdge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), planeProjector.recordedPoints());

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(qO1, qO2, qO3, qO4), polygonTriangulator.recordedPolygon().outer().points());
        assertEquals(1, polygonTriangulator.recordedPolygon().holes().size());
        assertEquals(List.of(qH1, qH2, qH3, qH4), polygonTriangulator.recordedPolygon().holes().get(0).points());
    }

    @Test
    void tessellate_planarFaceWithTwoHoles_appliesLoopCleanupIndependentlyPerHole() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom hole1Edge1 = new OrientedEdgeGeom("#oeHole1a", null, true);
        OrientedEdgeGeom hole1Edge2 = new OrientedEdgeGeom("#oeHole1b", null, true);
        OrientedEdgeGeom hole2Edge = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom hole1Bound = new LoopGeom("#hole1", List.of(hole1Edge1, hole1Edge2));
        LoopGeom hole2Bound = new LoopGeom("#hole2", List.of(hole2Edge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, hole1Bound, hole2Bound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(8.0, 0.0, 0.0);
        Point3 o3 = new Point3(8.0, 8.0, 0.0);
        Point3 o4 = new Point3(0.0, 8.0, 0.0);

        Point3 h11 = new Point3(1.0, 1.0, 0.0);
        Point3 h12 = new Point3(2.0, 1.0, 0.0);
        Point3 h13 = new Point3(2.0, 2.0, 0.0);
        Point3 h14 = new Point3(1.0, 2.0, 0.0);

        Point3 h21 = new Point3(5.0, 5.0, 0.0);
        Point3 h22 = new Point3(6.0, 5.0, 0.0);
        Point3 h23 = new Point3(6.0, 6.0, 0.0);
        Point3 h24 = new Point3(5.0, 6.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(8.0, 0.0);
        Point2 qO3 = new Point2(8.0, 8.0);
        Point2 qO4 = new Point2(0.0, 8.0);

        Point2 qH11 = new Point2(1.0, 1.0);
        Point2 qH12 = new Point2(2.0, 1.0);
        Point2 qH13 = new Point2(2.0, 2.0);
        Point2 qH14 = new Point2(1.0, 2.0);

        Point2 qH21 = new Point2(5.0, 5.0);
        Point2 qH22 = new Point2(6.0, 5.0);
        Point2 qH23 = new Point2(6.0, 6.0);
        Point2 qH24 = new Point2(5.0, 6.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));

        // hole 1: seam duplicate
        edgeDiscretizer.stubResult(hole1Edge1, List.of(h11, h12));
        edgeDiscretizer.stubResult(hole1Edge2, List.of(h12, h13, h14));

        // hole 2: closing duplicate
        edgeDiscretizer.stubResult(hole2Edge, List.of(h21, h22, h23, h24, h21));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);

        planeProjector.stubResult(h11, qH11);
        planeProjector.stubResult(h12, qH12);
        planeProjector.stubResult(h13, qH13);
        planeProjector.stubResult(h14, qH14);

        planeProjector.stubResult(h21, qH21);
        planeProjector.stubResult(h22, qH22);
        planeProjector.stubResult(h23, qH23);
        planeProjector.stubResult(h24, qH24);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();
        int[] t1 = new int[]{4, 5, 6};
        int[] t2 = new int[]{8, 9, 10};
        polygonTriangulator.stubResult(List.of(t1, t2));

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24),
                result.vertices()
        );
        assertEquals(2, result.triangles().size());
        assertSame(t1, result.triangles().get(0));
        assertSame(t2, result.triangles().get(1));

        assertEquals(List.of(outerEdge, hole1Edge1, hole1Edge2, hole2Edge), edgeDiscretizer.recordedEdges());
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24),
                planeProjector.recordedPoints()
        );

        assertNotNull(polygonTriangulator.recordedPolygon());
        assertEquals(List.of(qO1, qO2, qO3, qO4), polygonTriangulator.recordedPolygon().outer().points());
        assertEquals(2, polygonTriangulator.recordedPolygon().holes().size());
        assertEquals(List.of(qH11, qH12, qH13, qH14), polygonTriangulator.recordedPolygon().holes().get(0).points());
        assertEquals(List.of(qH21, qH22, qH23, qH24), polygonTriangulator.recordedPolygon().holes().get(1).points());
    }

    @Test
    void tessellate_planarFaceWithHoleOutsideOuter_throwsIllegalArgumentException() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge = new OrientedEdgeGeom("#oeHole", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, holeBound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(4.0, 0.0, 0.0);
        Point3 o3 = new Point3(4.0, 4.0, 0.0);
        Point3 o4 = new Point3(0.0, 4.0, 0.0);

        Point3 h1 = new Point3(5.0, 5.0, 0.0);
        Point3 h2 = new Point3(6.0, 5.0, 0.0);
        Point3 h3 = new Point3(6.0, 6.0, 0.0);
        Point3 h4 = new Point3(5.0, 6.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(4.0, 0.0);
        Point2 qO3 = new Point2(4.0, 4.0);
        Point2 qO4 = new Point2(0.0, 4.0);

        Point2 qH1 = new Point2(5.0, 5.0);
        Point2 qH2 = new Point2(6.0, 5.0);
        Point2 qH3 = new Point2(6.0, 6.0);
        Point2 qH4 = new Point2(5.0, 6.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge, List.of(h1, h2, h3, h4));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h1, qH1);
        planeProjector.stubResult(h2, qH2);
        planeProjector.stubResult(h3, qH3);
        planeProjector.stubResult(h4, qH4);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Hole loop must lie inside outer loop.", ex.getMessage());
        assertEquals(List.of(outerEdge, holeEdge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), planeProjector.recordedPoints());
        assertNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_planarFaceWithHoleIntersectingOuter_throwsIllegalArgumentException() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom holeEdge = new OrientedEdgeGeom("#oeHole", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom holeBound = new LoopGeom("#hole", List.of(holeEdge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, holeBound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(4.0, 0.0, 0.0);
        Point3 o3 = new Point3(4.0, 4.0, 0.0);
        Point3 o4 = new Point3(0.0, 4.0, 0.0);

        Point3 h1 = new Point3(3.0, 1.0, 0.0);
        Point3 h2 = new Point3(5.0, 1.0, 0.0);
        Point3 h3 = new Point3(5.0, 2.0, 0.0);
        Point3 h4 = new Point3(3.0, 2.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(4.0, 0.0);
        Point2 qO3 = new Point2(4.0, 4.0);
        Point2 qO4 = new Point2(0.0, 4.0);

        Point2 qH1 = new Point2(3.0, 1.0);
        Point2 qH2 = new Point2(5.0, 1.0);
        Point2 qH3 = new Point2(5.0, 2.0);
        Point2 qH4 = new Point2(3.0, 2.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(holeEdge, List.of(h1, h2, h3, h4));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h1, qH1);
        planeProjector.stubResult(h2, qH2);
        planeProjector.stubResult(h3, qH3);
        planeProjector.stubResult(h4, qH4);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Hole loop must not intersect outer loop.", ex.getMessage());
        assertEquals(List.of(outerEdge, holeEdge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4, h1, h2, h3, h4), planeProjector.recordedPoints());
        assertNull(polygonTriangulator.recordedPolygon());
    }

    @Test
    void tessellate_planarFaceWithIntersectingHoles_throwsIllegalArgumentException() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom hole1Edge = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom hole2Edge = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom hole1Bound = new LoopGeom("#hole1", List.of(hole1Edge));
        LoopGeom hole2Bound = new LoopGeom("#hole2", List.of(hole2Edge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, hole1Bound, hole2Bound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(8.0, 0.0, 0.0);
        Point3 o3 = new Point3(8.0, 8.0, 0.0);
        Point3 o4 = new Point3(0.0, 8.0, 0.0);

        Point3 h11 = new Point3(1.0, 1.0, 0.0);
        Point3 h12 = new Point3(4.0, 1.0, 0.0);
        Point3 h13 = new Point3(4.0, 4.0, 0.0);
        Point3 h14 = new Point3(1.0, 4.0, 0.0);

        Point3 h21 = new Point3(3.0, 3.0, 0.0);
        Point3 h22 = new Point3(6.0, 3.0, 0.0);
        Point3 h23 = new Point3(6.0, 6.0, 0.0);
        Point3 h24 = new Point3(3.0, 6.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(8.0, 0.0);
        Point2 qO3 = new Point2(8.0, 8.0);
        Point2 qO4 = new Point2(0.0, 8.0);

        Point2 qH11 = new Point2(1.0, 1.0);
        Point2 qH12 = new Point2(4.0, 1.0);
        Point2 qH13 = new Point2(4.0, 4.0);
        Point2 qH14 = new Point2(1.0, 4.0);

        Point2 qH21 = new Point2(3.0, 3.0);
        Point2 qH22 = new Point2(6.0, 3.0);
        Point2 qH23 = new Point2(6.0, 6.0);
        Point2 qH24 = new Point2(3.0, 6.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(hole1Edge, List.of(h11, h12, h13, h14));
        edgeDiscretizer.stubResult(hole2Edge, List.of(h21, h22, h23, h24));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h11, qH11);
        planeProjector.stubResult(h12, qH12);
        planeProjector.stubResult(h13, qH13);
        planeProjector.stubResult(h14, qH14);
        planeProjector.stubResult(h21, qH21);
        planeProjector.stubResult(h22, qH22);
        planeProjector.stubResult(h23, qH23);
        planeProjector.stubResult(h24, qH24);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Hole loops must not intersect each other.", ex.getMessage());
        assertEquals(List.of(outerEdge, hole1Edge, hole2Edge), edgeDiscretizer.recordedEdges());
        assertEquals(List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24), planeProjector.recordedPoints());
        assertNull(polygonTriangulator.recordedPolygon());
    }
    /*  */
    @Test
    void isPointStrictlyInsidePolygon_returnsTrue_forInteriorPoint() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(null, null, null, null);

        PolygonLoop2 polygon = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(4.0, 0.0),
                new Point2(4.0, 4.0),
                new Point2(0.0, 4.0)
        ));

        assertTrue(tessellator.isPointStrictlyInsidePolygon(new Point2(2.0, 2.0), polygon));
    }

    @Test
    void isPointStrictlyInsidePolygon_returnsFalse_forPointOnEdge() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(null, null, null, null);

        PolygonLoop2 polygon = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(4.0, 0.0),
                new Point2(4.0, 4.0),
                new Point2(0.0, 4.0)
        ));

        assertFalse(tessellator.isPointStrictlyInsidePolygon(new Point2(2.0, 0.0), polygon));
    }

    @Test
    void isPointStrictlyInsidePolygon_returnsFalse_forPointOnVertex() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(null, null, null, null);

        PolygonLoop2 polygon = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(4.0, 0.0),
                new Point2(4.0, 4.0),
                new Point2(0.0, 4.0)
        ));

        assertFalse(tessellator.isPointStrictlyInsidePolygon(new Point2(0.0, 0.0), polygon));
    }

    @Test
    void validateHoleInsideOuter_doesNotThrow_whenHoleIsStrictlyInside() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(null, null, null, null);

        PolygonLoop2 outer = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(6.0, 0.0),
                new Point2(6.0, 6.0),
                new Point2(0.0, 6.0)
        ));

        PolygonLoop2 hole = new PolygonLoop2(List.of(
                new Point2(1.0, 1.0),
                new Point2(2.0, 1.0),
                new Point2(2.0, 2.0),
                new Point2(1.0, 2.0)
        ));

        assertDoesNotThrow(() -> tessellator.validateHoleInsideOuter(outer, hole));
    }

    @Test
    void validateHoleInsideOuter_throws_whenHoleTouchesOuterEdge() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(null, null, null, null);

        PolygonLoop2 outer = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(6.0, 0.0),
                new Point2(6.0, 6.0),
                new Point2(0.0, 6.0)
        ));

        PolygonLoop2 hole = new PolygonLoop2(List.of(
                new Point2(1.0, 0.0),
                new Point2(2.0, 0.0),
                new Point2(2.0, 1.0),
                new Point2(1.0, 1.0)
        ));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateHoleInsideOuter(outer, hole)
        );

        assertEquals("Hole loop must lie inside outer loop.", ex.getMessage());
    }

    @Test
    void validateLoopsDoNotIntersect_doesNotThrow_forDisjointLoops() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(null, null, null, null);

        PolygonLoop2 first = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(2.0, 0.0),
                new Point2(2.0, 2.0),
                new Point2(0.0, 2.0)
        ));

        PolygonLoop2 second = new PolygonLoop2(List.of(
                new Point2(3.0, 3.0),
                new Point2(4.0, 3.0),
                new Point2(4.0, 4.0),
                new Point2(3.0, 4.0)
        ));

        assertDoesNotThrow(() ->
                tessellator.validateLoopsDoNotIntersect(first, second, "boom")
        );
    }


    @Test
    void validateHoleRelationships_throws_whenOneHoleIsNestedInsideAnother() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(null, null, null, null);

        PolygonLoop2 outer = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(10.0, 0.0),
                new Point2(10.0, 10.0),
                new Point2(0.0, 10.0)
        ));

        PolygonLoop2 hole1 = new PolygonLoop2(List.of(
                new Point2(2.0, 2.0),
                new Point2(8.0, 2.0),
                new Point2(8.0, 8.0),
                new Point2(2.0, 8.0)
        ));

        PolygonLoop2 hole2 = new PolygonLoop2(List.of(
                new Point2(4.0, 4.0),
                new Point2(5.0, 4.0),
                new Point2(5.0, 5.0),
                new Point2(4.0, 5.0)
        ));

        PolygonWithHoles2 polygon = new PolygonWithHoles2(outer, List.of(hole1, hole2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateHoleRelationships(polygon)
        );

        assertEquals("Hole loops must not contain each other.", ex.getMessage());
    }

    /*  */
    @Test
    void validateHoleRelationships_throws_whenOneHoleIsNestedInsideAnotherB() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonLoop2 outer = new PolygonLoop2(List.of(
                new Point2(0.0, 0.0),
                new Point2(10.0, 0.0),
                new Point2(10.0, 10.0),
                new Point2(0.0, 10.0)
        ));

        PolygonLoop2 hole1 = new PolygonLoop2(List.of(
                new Point2(2.0, 2.0),
                new Point2(8.0, 2.0),
                new Point2(8.0, 8.0),
                new Point2(2.0, 8.0)
        ));

        PolygonLoop2 hole2 = new PolygonLoop2(List.of(
                new Point2(4.0, 4.0),
                new Point2(5.0, 4.0),
                new Point2(5.0, 5.0),
                new Point2(4.0, 5.0)
        ));

        PolygonWithHoles2 polygon = new PolygonWithHoles2(outer, List.of(hole1, hole2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateHoleRelationships(polygon)
        );

        assertEquals("Hole loops must not contain each other.", ex.getMessage());
    }

    @Test
    void tessellate_planarFaceWithNestedHoles_throwsIllegalArgumentException() {
        OrientedEdgeGeom outerEdge = new OrientedEdgeGeom("#oeOuter", null, true);
        OrientedEdgeGeom hole1Edge = new OrientedEdgeGeom("#oeHole1", null, true);
        OrientedEdgeGeom hole2Edge = new OrientedEdgeGeom("#oeHole2", null, true);

        LoopGeom outerBound = new LoopGeom("#outer", List.of(outerEdge));
        LoopGeom hole1Bound = new LoopGeom("#hole1", List.of(hole1Edge));
        LoopGeom hole2Bound = new LoopGeom("#hole2", List.of(hole2Edge));

        FaceGeom face = new FaceGeom(
                "#f1",
                new PlaneSurface3(null),
                List.of(outerBound, hole1Bound, hole2Bound),
                true
        );

        Point3 o1 = new Point3(0.0, 0.0, 0.0);
        Point3 o2 = new Point3(10.0, 0.0, 0.0);
        Point3 o3 = new Point3(10.0, 10.0, 0.0);
        Point3 o4 = new Point3(0.0, 10.0, 0.0);

        Point3 h11 = new Point3(2.0, 2.0, 0.0);
        Point3 h12 = new Point3(8.0, 2.0, 0.0);
        Point3 h13 = new Point3(8.0, 8.0, 0.0);
        Point3 h14 = new Point3(2.0, 8.0, 0.0);

        Point3 h21 = new Point3(4.0, 4.0, 0.0);
        Point3 h22 = new Point3(5.0, 4.0, 0.0);
        Point3 h23 = new Point3(5.0, 5.0, 0.0);
        Point3 h24 = new Point3(4.0, 5.0, 0.0);

        Point2 qO1 = new Point2(0.0, 0.0);
        Point2 qO2 = new Point2(10.0, 0.0);
        Point2 qO3 = new Point2(10.0, 10.0);
        Point2 qO4 = new Point2(0.0, 10.0);

        Point2 qH11 = new Point2(2.0, 2.0);
        Point2 qH12 = new Point2(8.0, 2.0);
        Point2 qH13 = new Point2(8.0, 8.0);
        Point2 qH14 = new Point2(2.0, 8.0);

        Point2 qH21 = new Point2(4.0, 4.0);
        Point2 qH22 = new Point2(5.0, 4.0);
        Point2 qH23 = new Point2(5.0, 5.0);
        Point2 qH24 = new Point2(4.0, 5.0);

        RecordingEdgeDiscretizer edgeDiscretizer = new RecordingEdgeDiscretizer();
        edgeDiscretizer.stubResult(outerEdge, List.of(o1, o2, o3, o4));
        edgeDiscretizer.stubResult(hole1Edge, List.of(h11, h12, h13, h14));
        edgeDiscretizer.stubResult(hole2Edge, List.of(h21, h22, h23, h24));

        RecordingPlaneProjector planeProjector = new RecordingPlaneProjector();
        planeProjector.stubResult(o1, qO1);
        planeProjector.stubResult(o2, qO2);
        planeProjector.stubResult(o3, qO3);
        planeProjector.stubResult(o4, qO4);
        planeProjector.stubResult(h11, qH11);
        planeProjector.stubResult(h12, qH12);
        planeProjector.stubResult(h13, qH13);
        planeProjector.stubResult(h14, qH14);
        planeProjector.stubResult(h21, qH21);
        planeProjector.stubResult(h22, qH22);
        planeProjector.stubResult(h23, qH23);
        planeProjector.stubResult(h24, qH24);

        RecordingPolygonTriangulator polygonTriangulator = new RecordingPolygonTriangulator();

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                polygonTriangulator,
                planeProjector,
                null
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Hole loops must not contain each other.", ex.getMessage());
        assertEquals(List.of(outerEdge, hole1Edge, hole2Edge), edgeDiscretizer.recordedEdges());
        assertEquals(
                List.of(o1, o2, o3, o4, h11, h12, h13, h14, h21, h22, h23, h24),
                planeProjector.recordedPoints()
        );
        assertNull(polygonTriangulator.recordedPolygon());
    }



    @Test
    void validateHoleInsideOuter_throws_whenHoleTouchesOuterVertex() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonLoop2 outer = loop(
                new Point2(0.0, 0.0),
                new Point2(6.0, 0.0),
                new Point2(6.0, 6.0),
                new Point2(0.0, 6.0)
        );

        PolygonLoop2 hole = loop(
                new Point2(0.0, 0.0),
                new Point2(1.0, 0.0),
                new Point2(1.0, 1.0),
                new Point2(0.0, 1.0)
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateHoleInsideOuter(outer, hole)
        );

        assertEquals("Hole loop must lie inside outer loop.", ex.getMessage());
    }

    @Test
    void validateLoopsDoNotIntersect_throws_whenLoopsTouchAtEdge() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonLoop2 first = loop(
                new Point2(0.0, 0.0),
                new Point2(2.0, 0.0),
                new Point2(2.0, 2.0),
                new Point2(0.0, 2.0)
        );

        PolygonLoop2 second = loop(
                new Point2(2.0, 0.5),
                new Point2(3.0, 0.5),
                new Point2(3.0, 1.5),
                new Point2(2.0, 1.5)
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateLoopsDoNotIntersect(first, second, "touching edge")
        );

        assertEquals("touching edge", ex.getMessage());
    }

    @Test
    void validateLoopsDoNotIntersect_throws_whenLoopsTouchAtVertex() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonLoop2 first = loop(
                new Point2(0.0, 0.0),
                new Point2(2.0, 0.0),
                new Point2(2.0, 2.0),
                new Point2(0.0, 2.0)
        );

        PolygonLoop2 second = loop(
                new Point2(2.0, 2.0),
                new Point2(3.0, 2.0),
                new Point2(3.0, 3.0),
                new Point2(2.0, 3.0)
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateLoopsDoNotIntersect(first, second, "touching vertex")
        );

        assertEquals("touching vertex", ex.getMessage());
    }

    @Test
    void validateHoleRelationships_throws_whenHoleTouchesOuterAtVertex() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonLoop2 outer = loop(
                new Point2(0.0, 0.0),
                new Point2(6.0, 0.0),
                new Point2(6.0, 6.0),
                new Point2(0.0, 6.0)
        );

        PolygonLoop2 hole = loop(
                new Point2(0.0, 0.0),
                new Point2(1.0, 0.0),
                new Point2(1.0, 1.0),
                new Point2(0.0, 1.0)
        );

        PolygonWithHoles2 polygon = new PolygonWithHoles2(outer, List.of(hole));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateHoleRelationships(polygon)
        );

        assertEquals("Hole loop must not intersect outer loop.", ex.getMessage());
    }

    @Test
    void validateHoleRelationships_throws_whenHolesTouchAtVertex() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonLoop2 outer = loop(
                new Point2(0.0, 0.0),
                new Point2(10.0, 0.0),
                new Point2(10.0, 10.0),
                new Point2(0.0, 10.0)
        );

        PolygonLoop2 hole1 = loop(
                new Point2(1.0, 1.0),
                new Point2(4.0, 1.0),
                new Point2(4.0, 4.0),
                new Point2(1.0, 4.0)
        );

        PolygonLoop2 hole2 = loop(
                new Point2(4.0, 4.0),
                new Point2(6.0, 4.0),
                new Point2(6.0, 6.0),
                new Point2(4.0, 6.0)
        );

        PolygonWithHoles2 polygon = new PolygonWithHoles2(outer, List.of(hole1, hole2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateHoleRelationships(polygon)
        );

        assertEquals("Hole loops must not intersect each other.", ex.getMessage());
    }

    @Test
    void validateHoleRelationships_throws_whenHolesShareEdgeSegment() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                null,
                null,
                null,
                null
        );

        PolygonLoop2 outer = loop(
                new Point2(0.0, 0.0),
                new Point2(10.0, 0.0),
                new Point2(10.0, 10.0),
                new Point2(0.0, 10.0)
        );

        PolygonLoop2 hole1 = loop(
                new Point2(1.0, 1.0),
                new Point2(4.0, 1.0),
                new Point2(4.0, 4.0),
                new Point2(1.0, 4.0)
        );

        PolygonLoop2 hole2 = loop(
                new Point2(4.0, 2.0),
                new Point2(6.0, 2.0),
                new Point2(6.0, 3.0),
                new Point2(4.0, 3.0)
        );

        PolygonWithHoles2 polygon = new PolygonWithHoles2(outer, List.of(hole1, hole2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.validateHoleRelationships(polygon)
        );

        assertEquals("Hole loops must not intersect each other.", ex.getMessage());
    }


     /* ************************************************************************************ */

    @Test
    void simplifyProjectedLoop_removesImmediateBacktrack() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        List<Point2> input = List.of(
                new Point2(0.0, 0.0),
                new Point2(1.0, 0.0),
                new Point2(0.0, 0.0),
                new Point2(0.0, 1.0)
        );

        PlanarFaceTessellator.SimplifiedProjectedLoop result = tessellator.simplifyProjectedLoop(
                boundaryPoints3d(
                        input.get(0),
                        input.get(1),
                        input.get(2),
                        input.get(3)
                ),
                input
        );

        assertEquals(
                List.of(
                        new Point2(0.0, 0.0),
                        new Point2(0.0, 1.0)
                ),
                result.projectedPoints()
        );
    }

    @Test
    void simplifyProjectedLoop_removesCollinearMiddlePoint() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        List<Point2> input = List.of(
                new Point2(0.0, 0.0),
                new Point2(1.0, 0.0),
                new Point2(2.0, 0.0),
                new Point2(2.0, 1.0)
        );

        PlanarFaceTessellator.SimplifiedProjectedLoop result = tessellator.simplifyProjectedLoop(
                boundaryPoints3d(
                        input.get(0),
                        input.get(1),
                        input.get(2),
                        input.get(3)
                ),
                input
        );

        assertEquals(
                List.of(
                        new Point2(0.0, 0.0),
                        new Point2(2.0, 0.0),
                        new Point2(2.0, 1.0)
                ),
                result.projectedPoints()
        );
    }

    @Test
    void simplifyProjectedLoop_removesDuplicatesBacktracksAndCollinearPointsUntilStable() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        List<Point2> input = List.of(
                new Point2(0.0, 0.0),
                new Point2(1.0, 0.0),
                new Point2(0.0, 0.0),
                new Point2(0.0, 0.0),
                new Point2(0.0, 1.0),
                new Point2(0.0, 2.0),
                new Point2(1.0, 2.0)
        );

        PlanarFaceTessellator.SimplifiedProjectedLoop result = tessellator.simplifyProjectedLoop(
                boundaryPoints3d(
                        input.get(0),
                        input.get(1),
                        input.get(2),
                        input.get(3),
                        input.get(4),
                        input.get(5),
                        input.get(6)
                ),
                input
        );

        assertEquals(
                List.of(
                        new Point2(0.0, 0.0),
                        new Point2(0.0, 2.0),
                        new Point2(1.0, 2.0)
                ),
                result.projectedPoints()
        );
    }








    private PolygonLoop2 loop(Point2... points) {
        return new PolygonLoop2(List.of(points));
    }

    private FaceGeom planarFace(String stepId, LoopGeom... bounds) {
        return new FaceGeom(
                stepId,
                new PlaneSurface3(null),
                List.of(bounds),
                true
        );
    }

    private LoopGeom loop(String stepId) {
        return new LoopGeom(stepId, List.of());
    }

    private List<Point3> boundaryPoints3d(Point2... points) {
        List<Point3> result = new ArrayList<>();

        for (Point2 point : points) {
            result.add(new Point3(point.x(), point.y(), 0.0));
        }

        return result;
    }

    record SimplifiedProjectedLoop(
            List<Point3> boundaryPoints,
            List<Point2> projectedPoints
    ) {}
}

