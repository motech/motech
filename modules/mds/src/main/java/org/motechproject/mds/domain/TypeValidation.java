package org.motechproject.mds.domain;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * The <code>TypeValidation</code> contains a single validation option for the given type. This
 * class is related with table in database with the same name.
 */
@PersistenceCapable(identityType = IdentityType.DATASTORE)
public class TypeValidation {

    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    @PrimaryKey
    private Long id;

    @Persistent
    private String displayName;

    @Column(name = "TYPE_ID")
    private Type valueType;

    @Join
    @Element(column = "ANNOTATION")
    private List<Class<? extends Annotation>> annotations;

    public TypeValidation() {
        this(null, null);
    }

    public TypeValidation(String displayName, Type valueType) {
        this.displayName = displayName;
        this.valueType = valueType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Type getValueType() {
        return valueType;
    }

    public void setValueType(Type valueType) {
        this.valueType = valueType;
    }

    public List<Class<? extends Annotation>> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Class<? extends Annotation>> annotations) {
        this.annotations = annotations;
    }
}
