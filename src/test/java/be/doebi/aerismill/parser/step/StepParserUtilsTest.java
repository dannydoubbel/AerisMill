package be.doebi.aerismill.parser.step;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
class StepParserUtilsTest {
    @Test
    void parseStepBoolean_shouldParseTrueAndFalse() {
        assertTrue(StepParserUtils.parseStepBoolean(".T."));
        assertFalse(StepParserUtils.parseStepBoolean(".F."));
    }

    @Test
    void parseIntegerList_shouldParseCorrectly() {
        assertEquals(List.of(3, 3), StepParserUtils.parseIntegerList("(3,3)"));
    }

    @Test
    void parseDoubleList_shouldParseCorrectly() {
        assertEquals(List.of(0.0, 1.0), StepParserUtils.parseDoubleList("(0.0,1.0)"));
    }

    @Test
    void stripOuterParens_shouldStripCorrectly() {
        assertEquals("abc", StepParserUtils.stripOuterParens("(abc)"));
    }

    @Test
    void splitTopLevelGroups_shouldSplitOnlyTopLevelCommas() {
        List<String> result = StepParserUtils.splitTopLevelGroups("(#1,#2),(#3,#4)");
        assertEquals(List.of("(#1,#2)", "(#3,#4)"), result);
    }

    @Test
    void parseStepString_shouldParseQuotedString() {
        assertEquals("NONE", StepParserUtils.parseStepString("'NONE'"));
    }

    @Test
    void parseStepString_shouldReturnNullForDollar() {
        assertNull(StepParserUtils.parseStepString("$"));
    }

    @Test
    void splitTopLevelParameters_shouldSplitOnlyTopLevelCommas() {
        List<String> result = StepParserUtils.splitTopLevelParameters(
                "( 2, ( #1, #2, #3 ), .UNSPECIFIED., .F., .F. )"
        );

        assertEquals(
                List.of("2", "( #1, #2, #3 )", ".UNSPECIFIED.", ".F.", ".F."),
                result
        );
    }






}