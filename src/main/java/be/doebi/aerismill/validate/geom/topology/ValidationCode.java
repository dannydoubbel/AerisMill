package be.doebi.aerismill.validate.geom.topology;

public enum ValidationCode {
    LOOP_NULL,
    LOOP_EMPTY,
    LOOP_EDGE_NULL,
    LOOP_EDGE_START_NULL,
    LOOP_EDGE_END_NULL,
    LOOP_CONTINUITY_BROKEN,
    LOOP_NOT_CLOSED,
    FACE_NULL,
    FACE_NO_BOUNDS,
    FACE_BOUND_NULL,
    SHELL_NULL,
    SHELL_EMPTY,
    SHELL_FACE_NULL,
    SOLID_NULL,
    SOLID_OUTER_SHELL_NULL,
    SHELL_HAS_NO_FACES // not impelemted yet
}
