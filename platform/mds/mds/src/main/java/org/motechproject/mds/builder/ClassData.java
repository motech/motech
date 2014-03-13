package org.motechproject.mds.builder;

import org.motechproject.mds.domain.Entity;

import java.util.Arrays;

/**
 * Represents a class name and its byte code.
 */
public class ClassData {
    private final String className;
    private final String module;
    private final String namespace;
    private final byte[] bytecode;
    private final boolean interfaceClass;

    public ClassData(String className, byte[] bytecode) {
        this(className, bytecode, false);
    }

    public ClassData(String className, byte[] bytecode, boolean interfaceClass) {
        this(className, null, null, bytecode, interfaceClass);
    }

    public ClassData(Entity entity, byte[] bytecode) {
        this(entity, bytecode, false);
    }

    public ClassData(Entity entity, byte[] bytecode, boolean interfaceClass) {
        this(
                entity.getClassName(), entity.getModule(), entity.getNamespace(),
                bytecode, interfaceClass
        );
    }

    public ClassData(String className, String module, String namespace, byte[] bytecode) {
        this(className, module, namespace, bytecode, false);
    }

    public ClassData(String className, String module, String namespace, byte[] bytecode,
                     boolean interfaceClass) {
        this.className = className;
        this.module = module;
        this.namespace = namespace;
        this.bytecode = Arrays.copyOf(bytecode, bytecode.length);
        this.interfaceClass = interfaceClass;
    }

    public String getClassName() {
        return className;
    }

    public String getModule() {
        return module;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isInterfaceClass() {
        return interfaceClass;
    }

    public byte[] getBytecode() {
        return Arrays.copyOf(bytecode, bytecode.length);
    }

}
