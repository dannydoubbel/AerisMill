package be.doebi.aerismill.tessellation.polygon;

import be.doebi.aerismill.tessellation.face.PlanarFaceTessellator;

import java.util.List;

public final class RecordingPolygonTriangulator implements PolygonTriangulator {
    private PolygonWithHoles2 recordedPolygon;
    private List<int[]> result = List.of();

    @Override
    public PlanarFaceTessellator.TriangulationResult triangulateWithPoints(PolygonWithHoles2 polygon) {
        this.recordedPolygon = polygon;
        return new PlanarFaceTessellator.TriangulationResult(
                List.of(),
//                List.of(),
                result
        );
    }

    public void stubResult(List<int[]> result) {
        this.result = result;
    }

    public PolygonWithHoles2 recordedPolygon() {
        return recordedPolygon;
    }
}