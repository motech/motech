package org.motechproject.mds.builder;

import javassist.ClassPool;
import org.motechproject.mds.ex.EntityBuilderException;

import java.util.Arrays;

/**
 * The <code>EntityBuilder</code> is use to create a new empty class in a given class loader.
 */
public class EntityBuilder {
    /**
     * Constant <code>PACKAGE</code> presents a package for a new entities.
     */
    public static final String PACKAGE = "org.motechproject.mds.entity";

    private String className;
    private EntityClassLoader classLoader;
    private byte[] classBytes;

    public EntityBuilder withSimpleName(String simpleName) {
        this.className = String.format("%s.%s", PACKAGE, simpleName);
        this.classBytes = new byte[0];
        return this;
    }

    public EntityBuilder withClassLoader(ClassLoader classLoader) {
        this.classLoader = new EntityClassLoader(classLoader);
        this.classBytes = new byte[0];
        return this;
    }

    public String getClassName() {
        return className;
    }

    public byte[] getClassBytes() {
        if (null == classBytes) {
            classBytes = new byte[0];
        }

        return Arrays.copyOf(classBytes, classBytes.length);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void build() {
        try {
            classBytes = ClassPool.getDefault().makeClass(className).toBytecode();
            classLoader.defineClass(className, classBytes);
        } catch (Exception e) {
            throw new EntityBuilderException(e);
        }
    }

    /**
     * The <code>EntityClassLoader</code> class is a wrapper for {@link ClassLoader} and it is
     * used by {@link EntityBuilder} when new entity is built.
     */
    private static class EntityClassLoader extends ClassLoader {

        public EntityClassLoader(ClassLoader parent) {
            super(parent);
        }

        public void defineClass(String className, byte[] classBytes) {
            defineClass(className, classBytes, 0, classBytes.length);
        }

    }
}
