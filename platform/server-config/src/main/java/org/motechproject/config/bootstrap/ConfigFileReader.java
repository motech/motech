package org.motechproject.config.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * The <code>ConfigFileReader</code> class is used to load
 * <code>Properties</code> from a file.
 *
 * **/
public interface ConfigFileReader {
    /**
     * Reads a properties file and returns <code>Properties</code> object.
     * @param file - file containing properties to be read
     * **/
    Properties getProperties(File file) throws IOException;
}
