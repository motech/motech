package org.motechproject.config.core.datanucleus;

import java.util.Properties;

/**
 * Classes implementing this interface are responsible for loading loading datanucleus properties
 */
public interface DatanucleusManager {

    /**
     * Loads datanucleus configuration from several resources. It will try to load the configuration from file
     * from directory specified by the MOTECH_CONFIG_DIR environment variable, if it fails an attempt to load the
     * configuration from environmental variables will be made. If it also fails manager will try to load the
     * configuration from classpath, after copying from classpath configuration file is automatically saved to the
     * default config directory.
     *
     * @return the datanucleus properties
     * @see org.motechproject.config.core.environment.Environment
     */
    Properties getDatanucleusProperties();
}
