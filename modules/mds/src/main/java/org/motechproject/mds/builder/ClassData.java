package org.motechproject.mds.builder;

import org.apache.commons.lang.ArrayUtils;

import java.util.Arrays;

/**
 * Represents a class name and its byte code.
 */
public class ClassData {

    private final String className;
    private final byte[] bytecode;

    public ClassData(String className, byte[] bytecode) {
        this.className = className;
        this.bytecode = ArrayUtils.isNotEmpty(bytecode)
                ? Arrays.copyOf(bytecode, bytecode.length)
                : new byte[0];
    }

    public String getClassName() {
        return className;
    }

    public byte[] getBytecode() {
        return Arrays.copyOf(bytecode, getLength());
    }

    public int getLength() {
        return bytecode.length;
    }
}
