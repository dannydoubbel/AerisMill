package be.doebi.aerismill.parser.step;


import java.util.ArrayList;
import java.util.List;

public class StepParserUtils {
    private StepParserUtils() {
        // utility class
    }

    public static boolean parseStepBoolean(String value) {
        String trimmed = value.trim();

        if (".T.".equals(trimmed)) {
            return true;
        }
        if (".F.".equals(trimmed)) {
            return false;
        }

        throw new IllegalArgumentException("Invalid STEP boolean: " + value);
    }

    public static List<Integer> parseIntegerList(String value) {
        String inside = stripOuterParens(value);
        if (inside.isBlank()) {
            return List.of();
        }

        String[] pieces = inside.split(",");
        List<Integer> result = new ArrayList<>();

        for (String piece : pieces) {
            result.add(Integer.parseInt(piece.trim()));
        }

        return result;
    }

    public static List<Double> parseDoubleList(String value) {
        String inside = stripOuterParens(value);
        if (inside.isBlank()) {
            return List.of();
        }

        String[] pieces = inside.split(",");
        List<Double> result = new ArrayList<>();

        for (String piece : pieces) {
            result.add(Double.parseDouble(piece.trim()));
        }

        return result;
    }

    public static String stripOuterParens(String value) {
        String trimmed = value.trim();

        if (!trimmed.startsWith("(") || !trimmed.endsWith(")")) {
            throw new IllegalArgumentException("Expected parenthesized value but got: " + value);
        }

        return trimmed.substring(1, trimmed.length() - 1).trim();
    }

    public static List<String> splitTopLevelGroups(String text) {
        List<String> groups = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int depth = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            }

            if (c == ',' && depth == 0) {
                groups.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            groups.add(current.toString().trim());
        }

        return groups;
    }

    public static String parseStepString(String value) {
        String trimmed = value.trim();

        if ("$".equals(trimmed)) {
            return null;
        }

        if (trimmed.length() >= 2 && trimmed.startsWith("'") && trimmed.endsWith("'")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }

        throw new IllegalArgumentException("Invalid STEP string value: " + value);
    }

}
