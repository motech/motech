package org.motechproject.mds.domain;

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
        this.bytecode = Arrays.copyOf(bytecode, bytecode.length);
    }

    public int getLength() {
        return bytecode.length;
    }
}
