package org.motechproject.mds.domain;

import org.apache.commons.lang.ArrayUtils;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Unique;
import java.util.Arrays;

/**
 * The <code>ClassMapping</code> class contains bytecode of created entity class. This class is
 * related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class ClassMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    @Unique
    private String className;

    @Persistent
    private byte[] bytecode;

    public ClassMapping() {
        this(null, null);
    }

    public ClassMapping(String className, byte[] bytecode) {
        this.className = className;
        this.bytecode = ArrayUtils.isNotEmpty(bytecode)
                ? Arrays.copyOf(bytecode, bytecode.length)
                : new byte[0];
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public byte[] getBytecode() {
        return Arrays.copyOf(bytecode, getLength());
    }

    public void setBytecode(byte[] bytecode) {
        this.bytecode = ArrayUtils.isNotEmpty(bytecode)
                ? Arrays.copyOf(bytecode, bytecode.length)
                : new byte[0];
    }

    public int getLength() {
        return bytecode.length;
    }
}
