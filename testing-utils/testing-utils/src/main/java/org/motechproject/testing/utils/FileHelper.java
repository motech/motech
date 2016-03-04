package org.motechproject.testing.utils;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Helper for dealing with files during tests. Helps to ensure that tests are portable
 * between different OSs.
 */
public final class FileHelper {

    /**
     * Returns a file object for the given path. The path is converted to an URL before being
     * converted into a file, so some portability between different environments should be expected.
     * @param resourcePath the path the resource
     * @return the file repeseenting the resource
     */
    public static File getResourceFile(String resourcePath){
        URL url = FileHelper.class.getClassLoader().getResource(resourcePath);

        if (url == null) {
            return null;
        }

        try {
            return Paths.get(url.toURI()).toFile();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Unable to retrieve resource file: " + resourcePath, e);
        }
    }

    private FileHelper() {
    }
}
