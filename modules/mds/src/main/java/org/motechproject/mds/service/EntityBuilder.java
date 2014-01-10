package org.motechproject.mds.service;

import javassist.ClassPool;
import javassist.CtClass;
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
    private JDOClassLoader classLoader;
    private byte[] classBytes;

    public EntityBuilder withSingleName(String simpleName) {
        return withClassName(String.format("%s.%s", PACKAGE, simpleName));
    }

    public EntityBuilder withClassName(String className) {
        this.className = className;
        this.classBytes = new byte[0];
        return this;
    }

    public EntityBuilder withClassLoader(JDOClassLoader classLoader) {
        this.classLoader = classLoader;
        this.classBytes = new byte[0];
        return this;
    }

    public String getClassName() {
        return className;
    }

    public byte[] getClassBytes() {
        return Arrays.copyOf(classBytes, classBytes.length);
    }

    public JDOClassLoader getClassLoader() {
        return classLoader;
    }

    public void build() {
        try {
            CtClass ctClass = ClassPool.getDefault().makeClass(className);
            classBytes = ctClass.toBytecode();
            classLoader.defineClass(className, classBytes);
        } catch (Exception e) {
            throw new EntityBuilderException(e);
        }
    }
}
