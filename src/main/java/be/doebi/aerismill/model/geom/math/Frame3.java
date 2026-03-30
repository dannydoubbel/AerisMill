package be.doebi.aerismill.model.geom.math;

public record Frame3(
        Point3 origin,
        UnitVec3 xAxis,
        UnitVec3 yAxis,
        UnitVec3 zAxis
) {}