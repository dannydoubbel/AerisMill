package be.doebi.aerismill.tessellation.polygon;

import java.util.List;

public final class RecordingPolygonTriangulator implements PolygonTriangulator {
    private PolygonWithHoles2 recordedPolygon;
    private List<int[]> result = List.of();

    @Override
    public List<int[]> triangulate(PolygonWithHoles2 polygon) {
        this.recordedPolygon = polygon;
        return result;
    }

    public void stubResult(List<int[]> result) {
        this.result = result;
    }

    public PolygonWithHoles2 recordedPolygon() {
        return recordedPolygon;
    }
}