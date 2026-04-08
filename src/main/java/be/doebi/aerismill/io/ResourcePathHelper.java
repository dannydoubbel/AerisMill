package be.doebi.aerismill.io;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class ResourcePathHelper {

    public ResourcePathHelper() {
    }

    public static Path getResourceFolderPath(String folderName) {
        try {
            URL resource = Thread.currentThread()
                    .getContextClassLoader()
                    .getResource(folderName);

            if (resource == null) {
                throw new IllegalStateException("Resource folder not found: " + folderName);
            }

            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid resource URI for folder: " + folderName, e);
        }
    }
}
