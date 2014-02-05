package org.motechproject.mds.builder;

import javax.jdo.metadata.JDOMetadata;

/**
 * Apart from fields inherited from {@link org.motechproject.mds.builder.ClassData}, this class
 * also holds information about the JDO metadata for a class.
 */
public class EnhancedClassData extends ClassData {

    private final JDOMetadata jdoMetadata;

    public EnhancedClassData(String className, byte[] bytecode, JDOMetadata jdoMetadata) {
        super(className, bytecode);
        this.jdoMetadata = jdoMetadata;
    }

    public JDOMetadata getJdoMetadata() {
        return jdoMetadata;
    }
}
