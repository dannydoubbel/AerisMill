package be.doebi.aerismill.tessellation.face;

import be.doebi.aerismill.model.geom.math.Frame3;
import be.doebi.aerismill.model.geom.math.Point3;
import be.doebi.aerismill.model.geom.math.UnitVec3;
import be.doebi.aerismill.model.geom.math.Vec3;
import be.doebi.aerismill.model.geom.surface.ConicalSurface3;
import be.doebi.aerismill.model.geom.tolerance.GeometryTolerance;
import be.doebi.aerismill.model.geom.topology.EdgeGeom;
import be.doebi.aerismill.model.geom.topology.FaceGeom;
import be.doebi.aerismill.model.geom.topology.LoopGeom;
import be.doebi.aerismill.model.geom.topology.OrientedEdgeGeom;
import be.doebi.aerismill.model.geom.topology.VertexGeom;
import be.doebi.aerismill.tessellation.curve.DefaultEdgeDiscretizer;
import be.doebi.aerismill.tessellation.polygon.Point2;
import be.doebi.aerismill.tessellation.polygon.PolygonLoop2;
import be.doebi.aerismill.tessellation.polygon.RecordingPolygonTriangulator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConicalFaceTessellatorTest {

    @Test
    void projectBoundaryPointsTo2D_returnsDistinctProjectedPointsForSimpleConicalBand() {
        ConicalFaceTessellator tessellator = new ConicalFaceTessellator(
                new DefaultEdgeDiscretizer(),
                new RecordingPolygonTriangulator(),
                GeometryTolerance.defaults()
        );

        ConicalSurface3 cone = cone();

        List<Point3> boundary = List.of(
                cone.pointAt(0.0, 0.0),
                cone.pointAt(0.5, 0.0),
                cone.pointAt(0.5, 10.0),
                cone.pointAt(0.0, 10.0)
        );

        FaceGeom face = new FaceGeom("#f1", cone, List.of(), true);

        List<Point2> projected = tessellator.projectBoundaryPointsTo2D(cone, boundary, face, 0);

        assertEquals(4, projected.size());
        assertEquals(0.0, projected.get(0).y(), 1e-9);
        assertEquals(0.0, projected.get(1).y(), 1e-9);
        assertEquals(10.0, projected.get(2).y(), 1e-9);
        assertEquals(10.0, projected.get(3).y(), 1e-9);

        assertNotEquals(projected.get(0), projected.get(1));
        assertNotEquals(projected.get(1), projected.get(2));
        assertNotEquals(projected.get(2), projected.get(3));
    }

    @Test
    void prepareProjectedPolygonLoop_handlesSimpleConicalQuad() {
        RecordingPolygonTriangulator triangulator = new RecordingPolygonTriangulator();

        ConicalFaceTessellator tessellator = new ConicalFaceTessellator(
                new DefaultEdgeDiscretizer(),
                triangulator,
                GeometryTolerance.defaults()
        );

        ConicalSurface3 cone = cone();
        LoopGeom loop = rectangularLoopOnCone(cone, "#loop1");
        FaceGeom face = new FaceGeom("#f1", cone, List.of(loop), true);

        PlanarFaceTessellator.PreparedLoop prepared =
                tessellator.prepareProjectedPolygonLoop(face, loop, cone, 0);

        assertNotNull(prepared);
        assertEquals(4, prepared.boundaryPoints().size());
        assertNotNull(prepared.polygonLoop());
        assertEquals(4, prepared.polygonLoop().points().size());
    }

    @Test
    void tessellate_simpleConicalQuad_returnsPatch() {
        RecordingPolygonTriangulator triangulator = new RecordingPolygonTriangulator();
        triangulator.stubResult(List.of(
                new int[]{0, 1, 2},
                new int[]{0, 2, 3}
        ));

        ConicalFaceTessellator tessellator = new ConicalFaceTessellator(
                new DefaultEdgeDiscretizer(),
                triangulator,
                GeometryTolerance.defaults()
        );

        ConicalSurface3 cone = cone();
        LoopGeom loop = rectangularLoopOnCone(cone, "#loop1");
        FaceGeom face = new FaceGeom("#f1", cone, List.of(loop), true);

        FaceMeshPatch patch = tessellator.tessellate(face);

        assertNotNull(patch);
        assertEquals(4, patch.vertices().size());
        assertEquals(2, patch.triangles().size());

        PolygonLoop2 recordedOuter = triangulator.recordedPolygon().outer();
        assertNotNull(recordedOuter);
        assertEquals(4, recordedOuter.points().size());
    }

    @Test
    void tessellate_rejectsNonConicalFace() {
        RecordingPolygonTriangulator triangulator = new RecordingPolygonTriangulator();

        ConicalFaceTessellator tessellator = new ConicalFaceTessellator(
                new DefaultEdgeDiscretizer(),
                triangulator,
                GeometryTolerance.defaults()
        );

        FaceGeom nonConical = new FaceGeom("#f1", null, List.of(), true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tessellator.tessellate(nonConical)
        );

        assertTrue(ex.getMessage().contains("only conical faces are supported here"));
    }

    private ConicalSurface3 cone() {
        return new ConicalSurface3(
                new Frame3(
                        new Point3(0.0, 0.0, 0.0),
                        unit(1.0, 0.0, 0.0),
                        unit(0.0, 1.0, 0.0),
                        unit(0.0, 0.0, 1.0)
                ),
                10.0,
                Math.toRadians(10.0)
        );
    }

    private LoopGeom rectangularLoopOnCone(ConicalSurface3 cone, String loopId) {
        Point3 p0 = cone.pointAt(0.0, 0.0);
        Point3 p1 = cone.pointAt(0.5, 0.0);
        Point3 p2 = cone.pointAt(0.5, 10.0);
        Point3 p3 = cone.pointAt(0.0, 10.0);

        return new LoopGeom(loopId, List.of(
                orientedEdge("#oe1", "#e1", p0, p1),
                orientedEdge("#oe2", "#e2", p1, p2),
                orientedEdge("#oe3", "#e3", p2, p3),
                orientedEdge("#oe4", "#e4", p3, p0)
        ));
    }

    private OrientedEdgeGeom orientedEdge(String oeId, String eId, Point3 start, Point3 end) {
        EdgeGeom edge = new EdgeGeom(
                eId,
                lineCurve(start, end),
                new VertexGeom(eId + "_v1", start),
                new VertexGeom(eId + "_v2", end),
                true
        );

        return new OrientedEdgeGeom(oeId, edge, true);
    }

    private be.doebi.aerismill.model.geom.curve.LineCurve3 lineCurve(Point3 start, Point3 end) {
        return new be.doebi.aerismill.model.geom.curve.LineCurve3(
                start,
                UnitVec3.of(end.subtract(start))
        );
    }

    private UnitVec3 unit(double x, double y, double z) {
        return UnitVec3.of(new Vec3(x, y, z));
    }
}