package org.motechproject.mds.service;

/**
 * The <code>JDOClassLoader</code> class is a wrapper for {@link java.lang.ClassLoader} and it is
 * used in mds services.
 */
public class JDOClassLoader extends ClassLoader {
    public static final JDOClassLoader PERSISTANCE_CLASS_LOADER = new JDOClassLoader();

    public JDOClassLoader() {
        this(JDOClassLoader.class.getClassLoader());
    }

    public JDOClassLoader(ClassLoader parent) {
        super(parent);
    }

    public void defineClass(String className, byte[] classBytes) {
        defineClass(className, classBytes, 0, classBytes.length);
    }
}
