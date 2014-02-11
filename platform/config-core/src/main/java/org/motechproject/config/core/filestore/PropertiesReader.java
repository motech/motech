package org.motechproject.config.core.filestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A utility class for loading properties from a file.
 */
public final class PropertiesReader {

    public static Properties getProperties(File file) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        }
        return properties;
    }

    private PropertiesReader() {
    }
}
