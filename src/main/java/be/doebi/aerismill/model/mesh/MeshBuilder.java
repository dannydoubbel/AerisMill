package be.doebi.aerismill.model.mesh;

import be.doebi.aerismill.model.geom.math.Point3;

public interface MeshBuilder {
    int addVertex(Point3 point);
    void addTriangle(int a, int b, int c);
    Mesh build();
}