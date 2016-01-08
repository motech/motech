package org.motechproject.mds.reflections;

import org.osgi.framework.Bundle;

/**
 * The <code>WrappedBundleClassLoader</code> class is a class that wraps bundle class loader so that it can be used
 * during annotations processing.
 *
 * @see org.motechproject.mds.reflections.ReflectionsUtil
 */
public class WrappedBundleClassLoader extends ClassLoader {

    private Bundle bundle;

    public WrappedBundleClassLoader(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (null != loadedClass) {
            return loadedClass;
        } else {
            return bundle.loadClass(name);
        }
    }
}
