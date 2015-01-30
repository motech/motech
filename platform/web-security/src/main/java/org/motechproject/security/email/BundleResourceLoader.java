package org.motechproject.security.email;

import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;

/**
 * A resource loader required to properly load velocity templates from classpath in OSGi.
 * When retrieving the stream, we need to use this bundle's {@link ClassLoader}, instead of the one coming
 * from the Velocity bundle.
 */
public class BundleResourceLoader extends ClasspathResourceLoader {

    /**
     * Loads resource with given name
     *
     * @param name of resource - supports fully qualified URLs
     *             and classpath pseudo-URLs
     * @return InputStream with resource
     */
    @Override
    public InputStream getResourceStream(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }
}
