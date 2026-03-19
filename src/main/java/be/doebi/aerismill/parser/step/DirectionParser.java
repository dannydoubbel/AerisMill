package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.geometry.Direction;

import java.util.ArrayList;
import java.util.List;

public class DirectionParser {
    public static Direction parse(String id, String rawParameters) {
        String trimmed = rawParameters.trim();

        int firstComma = trimmed.indexOf(",");
        String namePart = trimmed.substring(0, firstComma).trim();
        String ratiosPart = trimmed.substring(firstComma + 1).trim();

        String name = unquote(namePart);

        int start = ratiosPart.indexOf('(');
        int end = ratiosPart.lastIndexOf(')');
        String inside = ratiosPart.substring(start + 1, end).trim();

        String[] pieces = inside.split(",");
        List<Double> directionRatios = new ArrayList<>();

        for (String piece : pieces) {
            directionRatios.add(Double.parseDouble(piece.trim()));
        }

        return new Direction(id, rawParameters, name, directionRatios);
    }

    private static String unquote(String text) {
        text = text.trim();
        if (text.startsWith("'") && text.endsWith("'") && text.length() >= 2) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }
}
