package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.surface.PlaneSurface3;
import be.doebi.aerismill.model.geom.surface.Surface3;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.tessellation.curve.EdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.Point2;
import be.doebi.aerismill.tessellation.polygon.PolygonLoop2;
import be.doebi.aerismill.tessellation.polygon.PolygonTriangulator;
import be.doebi.aerismill.tessellation.projection.DefaultPlaneProjector;
import be.doebi.aerismill.tessellation.projection.PlaneProjector;
import org.junit.jupiter.api.Test;

import java.util.List;

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

        assertEquals("Face must have at least one bound.", ex.getMessage());
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

        assertEquals("Only planar faces are supported for now.", exception.getMessage());
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

        assertEquals("Face must have at least one bound.", exception.getMessage());
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

        assertEquals("Face bound must contain at least one edge.", ex.getMessage());
    }

    @Test
    void tessellate_planarFaceWithMultipleBounds_throwsIllegalArgumentException() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        LoopGeom loop1 = new LoopGeom(
                "#loop1",
                List.of()
        );

        LoopGeom loop2 = new LoopGeom(
                "#loop2",
                List.of()
        );

        FaceGeom face = new FaceGeom(
                "#1",
                new PlaneSurface3(null),
                List.of(loop1, loop2),
                true
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(face)
        );

        assertEquals("Only single-bound planar faces are supported for now.", exception.getMessage());
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

        assertEquals("Face bound must contain at least one edge.", exception.getMessage());
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

        assertEquals("Face bound must contain at least one non-null edge.", exception.getMessage());
    }

    @Test
    void tessellate_shouldDiscretizeFirstNonNullEdgeOfFirstBound_andStillReturnEmptyPatch() {
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
        GeometryTolerance tolerance = null; // replace with a real instance if easy

        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                edgeDiscretizer,
                null,
                null,
                tolerance
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertSame(firstEdge, edgeDiscretizer.recordedEdge());
        assertSame(tolerance, edgeDiscretizer.recordedTolerance());
        assertNotNull(result);
    }


    private static final class RecordingEdgeDiscretizer implements EdgeDiscretizer {
        private OrientedEdgeGeom recordedEdge;
        private GeometryTolerance recordedTolerance;

        @Override
        public List<Point3> discretize(OrientedEdgeGeom edge, GeometryTolerance tolerance) {
            this.recordedEdge = edge;
            this.recordedTolerance = tolerance;
            return List.of();
        }

        OrientedEdgeGeom recordedEdge() {
            return recordedEdge;
        }

        GeometryTolerance recordedTolerance() {
            return recordedTolerance;
        }
    }

    @Test
    void tessellate_validPlanarFace_returnsEmptyPatchForNow() {
        PlanarFaceTessellator tessellator = new PlanarFaceTessellator(
                stubEdgeDiscretizer(),
                stubPolygonTriangulator(),
                stubPlaneProjector(),
                GeometryTolerance.defaults()
        );

        OrientedEdgeGeom edge = new OrientedEdgeGeom("#oe1", null, true);

        LoopGeom loop = new LoopGeom(
                "#loop1",
                List.of(edge)
        );

        FaceGeom face = new FaceGeom(
                "#1",
                new PlaneSurface3(null),
                List.of(loop),
                true
        );

        FaceMeshPatch result = tessellator.tessellate(face);

        assertNotNull(result);
        assertNotNull(result.vertices());
        assertNotNull(result.triangles());
        assertTrue(result.vertices().isEmpty());
        assertTrue(result.triangles().isEmpty());
    }
}

