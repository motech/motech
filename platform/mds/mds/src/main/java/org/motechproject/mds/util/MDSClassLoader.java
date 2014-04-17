package org.motechproject.mds.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <code>MDSClassLoader</code> class is a mds wrapper for {@link ClassLoader}.
 */
public class MDSClassLoader extends ClassLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(MDSClassLoader.class);

    private static class Holder {
        private static MDSClassLoader instance = new MDSClassLoader();
    }

    public static MDSClassLoader getInstance() {
        return Holder.instance;
    }

    public static MDSClassLoader getStandaloneInstance() {
        return new MDSClassLoader();
    }

    public static MDSClassLoader getStandaloneInstance(ClassLoader parent) {
        return new MDSClassLoader(parent);
    }

    public static void reloadClassLoader() {
        Holder.instance = new MDSClassLoader();

        if (Thread.currentThread().getContextClassLoader() instanceof MDSClassLoader) {
            LOGGER.debug("The context class loader of the current thread is instance of {}", MDSClassLoader.class.getName());

            Thread.currentThread().setContextClassLoader(Holder.instance);
            LOGGER.info("The context class loader of the current thread was reloaded");
        }

        LOGGER.info("The MDS class loader was reloaded");
    }

    protected MDSClassLoader() {
        this(MDSClassLoader.class.getClassLoader());
    }

    protected MDSClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> safeDefineClass(String name, byte[] bytecode) {
        try {
            return loadClass(name);
        } catch (ClassNotFoundException e) {
            // the class should be defined in the MDS class loader only if it does not exist
            return defineClass(name, bytecode, 0, bytecode.length);
        }
    }

}
