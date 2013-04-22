package org.motechproject.admin.email;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.InputStream;

public class BundleResourceLoader extends ClasspathResourceLoader {

    @Override
    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        return this.getClass().getClassLoader().getResourceAsStream(name);
    }
}
