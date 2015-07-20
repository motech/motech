package org.motechproject.mds.reflections;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import org.apache.commons.io.IOUtils;
import org.motechproject.mds.ex.loader.LoaderException;
import org.motechproject.mds.javassist.MotechClassPool;
import org.motechproject.mds.util.JavassistUtil;
import org.osgi.framework.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The <code>PristineBundleClassLoader</code> class is an implementation of custom class loader that allows to load
 * classes directly from its origin bundle jar, so that they are not modified by MDS enhancer, which is important during
 * annotation processing. It is also responsible for appending discovered classes to javassist classpath so that
 * they are accessible at the entities construction time.
 *
 * @see org.motechproject.mds.annotations.internal.EntityProcessor
 * @see org.motechproject.mds.builder.impl.EntityBuilderImpl
 */
public class PristineBundleClassLoader extends ClassLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PristineBundleClassLoader.class);

    private Bundle bundle;
    private ClassPool classPool;

    protected PristineBundleClassLoader(Bundle bundle) {
        this.bundle = bundle;
        this.classPool = MotechClassPool.getDefault();
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> loadedClass = findLoadedClass(name);
        if (null != loadedClass) {
            return loadedClass;
        }

        loadedClass = loadClassFromClassPoolContext(name);
        if (null != loadedClass) {
            return loadedClass;
        }

        loadedClass = loadClassFromBundle(name);
        if (null != loadedClass) {
            return loadedClass;
        }

        throw new ClassNotFoundException(String.format("PristineBundleClassLoader of the %s bundle cannot find class: %s.",
                bundle.getSymbolicName(), name));
    }

    private Class<?> loadClassFromClassPoolContext(String name) {
        try {
            return classPool.getClassLoader().loadClass(name);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private Class<?> loadClassFromBundle(String name) {
        try {
            bundle.loadClass(name);
            byte[] bytecode = null;

            if (!isClassAppendedToClassPath(name)) {
                // if the loaded class cannot be found in class pool, we have to
                // append this class to the javassist class path
                bytecode = loadPristineClassBytecode(name);
                appendClassPath(name, bytecode);
            }

            LOGGER.debug("Loading pristine entity class: " + name);
            bytecode = null == bytecode ? loadPristineClassBytecode(name) : bytecode;
            return defineClass(name, bytecode, 0, bytecode.length);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private boolean isClassAppendedToClassPath(String name) {
        return null != classPool.getOrNull(name);
    }

    private void appendClassPath(String name, byte[] bytecode) {
        LOGGER.debug("Appending class to javassist classpath: " + name);
        ByteArrayClassPath classPath = new ByteArrayClassPath(name, bytecode);
        classPool.appendClassPath(classPath);
    }

    private byte[] loadPristineClassBytecode(String name) {
        String classpath = JavassistUtil.toClassPath(name);
        URL classResource = bundle.getResource(classpath);

        if (classResource != null) {
            try (InputStream in = classResource.openStream()) {
                return IOUtils.toByteArray(in);
            } catch (IOException e) {
                throw new LoaderException(e);
            }
        }

        return null;
    }
}
