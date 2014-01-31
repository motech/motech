package org.motechproject.security.email;

import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;

public class BundleResourceLoader extends ClasspathResourceLoader {

    @Override
    public InputStream getResourceStream(String name) {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }
}
