package org.motechproject.mds.util;

/**
 * The <code>MDSClassLoader</code> class is a mds wrapper for {@link ClassLoader}.
 */
public class MDSClassLoader extends ClassLoader {
    private static class Holder {
        private static MDSClassLoader instance = new MDSClassLoader();
    }

    public static MDSClassLoader getInstance() {
        return Holder.instance;
    }

    public static void reloadClassLoader() {
        Holder.instance = new MDSClassLoader();

        if (Thread.currentThread().getContextClassLoader() instanceof MDSClassLoader) {
            Thread.currentThread().setContextClassLoader(Holder.instance);
        }
    }

    public MDSClassLoader() {
        this(MDSClassLoader.class.getClassLoader());
    }

    public MDSClassLoader(ClassLoader parent) {
        super(parent);
    }

    public Class<?> defineClass(String name, byte[] bytecode) {
        return defineClass(name, bytecode, 0, bytecode.length);
    }

}
