package org.motechproject.mds.domain;

import org.motechproject.mds.dto.MetadataDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class FieldMetadata {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private Field field;

    @Persistent
    private String key;

    @Persistent
    private String value;

    public FieldMetadata() {
        this(null, null);
    }

    public FieldMetadata(Field field, String key) {
        this(field, key, null);
    }

    public FieldMetadata(MetadataDto metadata) {
        update(metadata);
    }

    public FieldMetadata(Field field, String key, String value) {
        this.field = field;
        this.key = key;
        this.value = value;
    }

    public MetadataDto toDto() {
        return new MetadataDto(id, key, value);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public final void update(MetadataDto metadata) {
        key = metadata.getKey();
        value = metadata.getValue();
    }

    @NotPersistent
    public FieldMetadata copy() {
        FieldMetadata copy = new FieldMetadata();

        copy.setKey(key);
        copy.setValue(value);

        return copy;
    }
}
