package org.motechproject.mds.builder;

/**
 * The <code>MDSClassLoader</code> class is a mds wrapper for {@link ClassLoader}.
 */
public class MDSClassLoader extends ClassLoader {
    public static final MDSClassLoader PERSISTANCE = new MDSClassLoader();

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
