package be.doebi.aerismill.assemble.step.geom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultStepToGeomAssemblerTest {

    @Test
    void assemble_nullStepModel_returnsErrorIssue() {
        DefaultStepToGeomAssembler assembler = new DefaultStepToGeomAssembler(null, null);

        AssemblyResult result = assembler.assemble(null);

        assertNotNull(result);
        assertNotNull(result.solids());
        assertNotNull(result.issues());
        assertEquals(0, result.solids().size());
        assertEquals(1, result.issues().size());

        AssemblyIssue issue = result.issues().getFirst();
        assertNull(issue.stepId());
        assertEquals(AssemblyIssueSeverity.ERROR, issue.severity());
        assertEquals(AssemblyIssueCode.NULL_STEP_MODEL, issue.code());
        assertEquals("StepModel must not be null", issue.message());
    }
}