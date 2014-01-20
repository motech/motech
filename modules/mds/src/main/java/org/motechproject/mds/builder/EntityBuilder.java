package org.motechproject.mds.builder;

import org.apache.commons.lang.ArrayUtils;
import org.motechproject.mds.ex.EntityBuilderException;
import org.motechproject.mds.javassist.MotechClassPool;

import java.util.Arrays;

import static org.motechproject.mds.constants.Constants.Packages;

/**
 * The <code>EntityBuilder</code> is use to create a new empty class in a given class loader.
 */
public class EntityBuilder {
    private String className;
    private MDSClassLoader classLoader;
    private byte[] classBytes;

    public EntityBuilder withSimpleName(String simpleName) {
        return withClassName(String.format("%s.%s", Packages.ENTITY, simpleName));
    }

    public EntityBuilder withClassName(String className) {
        this.className = className;
        this.classBytes = null;
        return this;
    }

    public EntityBuilder withClassLoader(ClassLoader classLoader) {
        this.classLoader = new MDSClassLoader(classLoader);
        this.classBytes = null;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public byte[] getClassBytes() {
        return ArrayUtils.isNotEmpty(classBytes)
                ? Arrays.copyOf(classBytes, classBytes.length)
                : new byte[0];

    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void build() {
        try {
            classBytes = MotechClassPool.getDefault().makeClass(className).toBytecode();
            classLoader.defineClass(className, classBytes);
        } catch (Exception e) {
            throw new EntityBuilderException(e);
        }
    }

}
