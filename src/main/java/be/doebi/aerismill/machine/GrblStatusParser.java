package be.doebi.aerismill.machine;

public class GrblStatusParser {

    public MachineStatus parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        line = line.trim();

        if (!line.startsWith("<") || !line.endsWith(">")) {
            return null;
        }

        String content = line.substring(1, line.length() - 1); // remove < and >
        String[] parts = content.split("\\|");

        if (parts.length == 0) {
            return null;
        }

        String state = parts[0].trim();

        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double a = 0.0;

        for (String part : parts) {
            part = part.trim();

            if (part.startsWith("MPos:")) {
                String valuePart = part.substring("MPos:".length());
                String[] coords = valuePart.split(",");

                if (coords.length > 0) x = Double.parseDouble(coords[0].trim());
                if (coords.length > 1) y = Double.parseDouble(coords[1].trim());
                if (coords.length > 2) z = Double.parseDouble(coords[2].trim());
                if (coords.length > 3) a = Double.parseDouble(coords[3].trim());
            }
        }

        return new MachineStatus(state, x, y, z, a);
    }
}