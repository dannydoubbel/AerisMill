package be.doebi.aerismill.assemble.step.geom;

import be.doebi.aerismill.model.step.base.StepModel;

public interface StepToGeomAssembler {
    AssemblyResult assemble(StepModel stepModel);
}