package org.motechproject.mds.reflections;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import org.apache.commons.io.IOUtils;
import org.motechproject.mds.exception.loader.LoaderException;
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
 * they are accessible at the entities construction time. It will skip the procedure of appending classpath for some classes,
 * coming from packages, to which we are certain they will not contain any MDS entities. This is dictated by the need to load
 * these classes from their original bundle during annotation processing.
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
        Class<?> loadedClass;

        if (mustLoadFromOriginalBundle(name)) {
            loadedClass = bundle.loadClass(name);

            if (null != loadedClass) {
                return loadedClass;
            }
        }

        loadedClass = findLoadedClass(name);
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
            LOGGER.debug("Unable to find class {}", name , e);
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
            int length = null == bytecode ? 0 : bytecode.length;

            return defineClass(name, bytecode, 0, length);
        } catch (ClassNotFoundException e) {
            LOGGER.debug("Unable to find class {}", name , e);
            return null;
        }
    }

    private boolean isClassAppendedToClassPath(String name) {
        return null != classPool.getOrNull(name);
    }

    private void appendClassPath(String name, byte[] bytecode) {
        LOGGER.debug("Appending class to Javassist classpath: " + name);
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
                throw new LoaderException("Unable to load bytes in pristine class", e);
            }
        }

        return null;
    }

    private boolean mustLoadFromOriginalBundle(String className) {
        // Classes that we use during annotation processing must be loaded from their original bundle
        // to avoid ClassCastExceptions in annotation processors
        return className.startsWith("org.motechproject.mds.annotations") ||
                className.startsWith("org.motechproject.mds.event") ||
                className.startsWith("javax.jdo.annotations") ||
                className.startsWith("javax.validation.constraints");
    }
}
