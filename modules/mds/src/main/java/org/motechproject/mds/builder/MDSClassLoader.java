package org.motechproject.mds.builder;

/**
 * The <code>MDSClassLoader</code> class is a mds wrapper for {@link ClassLoader}.
 */
public class MDSClassLoader extends ClassLoader {
    private static MDSClassLoader instance = new MDSClassLoader();

    public static MDSClassLoader getInstance() {
        return instance;
    }

    public static void reloadClassLoader() {
        instance = new MDSClassLoader();
        if (Thread.currentThread().getContextClassLoader() instanceof MDSClassLoader) {
            Thread.currentThread().setContextClassLoader(instance);
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

    public Class<?> defineClass(ClassData classData) {
        return defineClass(classData.getClassName(), classData.getBytecode());
    }
}
