package org.motechproject.mds.osgi;

import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;

/**
 * Loads resources for Velocity from the MDS bundle.
 */
public class VelocityResourceLoader extends ClasspathResourceLoader {

    @Override
    public InputStream getResourceStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }
}
