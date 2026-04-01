package be.doebi.aerismill.validate.geom.topology;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationReportTest {
    @Test
    void newReport_hasZeroErrorAndWarningCount() {
        ValidationReport report = new ValidationReport();

        assertTrue(report.isEmpty());
        assertTrue(report.isValid());
        assertFalse(report.hasErrors());
        assertEquals(0, report.errorCount());
        assertEquals(0, report.warningCount());
    }

    @Test
    void addError_incrementsErrorCount() {
        ValidationReport report = new ValidationReport();

        report.addError(
                ValidationCode.LOOP_EMPTY,
                "#100",
                "LoopGeom must contain at least one oriented edge"
        );

        assertFalse(report.isEmpty());
        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
        assertEquals(1, report.errorCount());
        assertEquals(0, report.warningCount());
        assertEquals(1, report.messages().size());
    }

    @Test
    void addWarning_incrementsWarningCount() {
        ValidationReport report = new ValidationReport();

        report.addWarning(
                ValidationCode.LOOP_EMPTY,
                "#100",
                "This is only a warning"
        );

        assertFalse(report.isEmpty());
        assertTrue(report.isValid());
        assertFalse(report.hasErrors());
        assertEquals(0, report.errorCount());
        assertEquals(1, report.warningCount());
        assertEquals(1, report.messages().size());
    }

    @Test
    void addAll_mergesMessagesFromOtherReport() {
        ValidationReport report1 = new ValidationReport();
        report1.addError(
                ValidationCode.LOOP_EMPTY,
                "#100",
                "LoopGeom must contain at least one oriented edge"
        );

        ValidationReport report2 = new ValidationReport();
        report2.addWarning(
                ValidationCode.FACE_NO_BOUNDS,
                "#200",
                "FaceGeom should contain at least one bound"
        );

        report1.addAll(report2);

        assertEquals(2, report1.messages().size());
        assertEquals(1, report1.errorCount());
        assertEquals(1, report1.warningCount());
        assertFalse(report1.isValid());
        assertTrue(report1.hasErrors());
    }

    @Test
    void addAll_nullDoesNothing() {
        ValidationReport report = new ValidationReport();
        report.addError(
                ValidationCode.LOOP_EMPTY,
                "#100",
                "LoopGeom must contain at least one oriented edge"
        );

        report.addAll(null);

        assertEquals(1, report.messages().size());
        assertEquals(1, report.errorCount());
        assertEquals(0, report.warningCount());
        assertFalse(report.isValid());
        assertTrue(report.hasErrors());
    }
}