package be.doebi.aerismill.parser.step;

import be.doebi.aerismill.model.step.geometry.CartesianPoint;

import java.util.ArrayList;
import java.util.List;

public class CartesianPointParser {
    public static CartesianPoint parse(String id, String rawParameters) {
        String trimmed = rawParameters.trim();

        int firstComma = trimmed.indexOf(",");
        String namePart = trimmed.substring(0, firstComma).trim();
        String coordsPart = trimmed.substring(firstComma + 1).trim();

        String name = unquote(namePart);

        int start = coordsPart.indexOf('(');
        int end = coordsPart.lastIndexOf(')');
        String inside = coordsPart.substring(start + 1, end).trim();

        String[] pieces = inside.split(",");
        List<Double> coordinates = new ArrayList<>();

        for (String piece : pieces) {
            coordinates.add(Double.parseDouble(piece.trim()));
        }

        return new CartesianPoint(id, rawParameters, name, coordinates);
    }

    private static String unquote(String text) {
        text = text.trim();
        if (text.startsWith("'") && text.endsWith("'") && text.length() >= 2) {
            return text.substring(1, text.length() - 1);
        }
        return text;
    }
}
