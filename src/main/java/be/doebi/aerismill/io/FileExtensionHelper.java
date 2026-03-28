package be.doebi.aerismill.io;

import java.nio.file.Path;
import java.util.Locale;

public class FileExtensionHelper {

    //public static boolean isStlFile(Path path)
    //public static boolean isGcodeFile(Path path)
    private FileExtensionHelper() {
    }

    public static boolean isStepFile(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".step") || name.endsWith(".stp");
    }
}
