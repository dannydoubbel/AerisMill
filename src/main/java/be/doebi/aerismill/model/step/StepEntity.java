package be.doebi.aerismill.model.step;

/*
STEP roadmap

Phase 1 - absolute skeleton
[X] 1. CARTESIAN_POINT
[X] 2. DIRECTION
[X] 3. AXIS2_PLACEMENT_3D
[X] 4. VERTEX_POINT
[X] 5. LINE
[X] 6. CIRCLE
[X] 7. EDGE_CURVE
[X] 8. ORIENTED_EDGE
[X] 9. EDGE_LOOP
[X] 10. FACE_BOUND
[ ] 11. FACE_OUTER_BOUND
[ ] 12. PLANE
[ ] 13. CYLINDRICAL_SURFACE
[ ] 14. ADVANCED_FACE
[ ] 15. CLOSED_SHELL
[ ] 16. MANIFOLD_SOLID_BREP
[ ] 17. ADVANCED_BREP_SHAPE_REPRESENTATION

Phase 2 - wider real-world coverage
[X] 18. VECTOR
[ ] 19. CONICAL_SURFACE
[ ] 20. TOROIDAL_SURFACE
[ ] 21. B_SPLINE_CURVE_WITH_KNOTS
[ ] 22. B_SPLINE_SURFACE_WITH_KNOTS
[ ] 23. COMPLEX_ENTITY

Phase 3 - product/shape root plumbing
[ ] 24. SHAPE_REPRESENTATION
[ ] 25. SHAPE_DEFINITION_REPRESENTATION
[ ] 26. PRODUCT_DEFINITION_SHAPE
*/

public class StepEntity {
    private final String id;
    private final String type;
    private final String rawParameters;

    public StepEntity(String id, String type, String rawParameters) {
        this.id = id;
        this.type = type;
        this.rawParameters = rawParameters;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getRawParameters() {
        return rawParameters;
    }

    @Override
    public String toString() {
        return "StepEntity{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", rawParameters='" + rawParameters + '\'' +
                '}';
    }
}
