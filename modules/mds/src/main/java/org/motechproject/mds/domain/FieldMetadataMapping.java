package org.motechproject.mds.domain;

import org.motechproject.mds.dto.MetadataDto;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.DATASTORE, detachable = "true")
public class FieldMetadataMapping {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.INCREMENT)
    private Long id;

    @Persistent
    private FieldMapping field;

    @Persistent
    private String key;

    @Persistent
    private String value;

    public FieldMetadataMapping() {
        this(null, null);
    }

    public FieldMetadataMapping(FieldMapping field, String key) {
        this(field, key, null);
    }

    public FieldMetadataMapping(FieldMapping field, String key, String value) {
        this.field = field;
        this.key = key;
        this.value = value;
    }

    public MetadataDto toDto() {
        return new MetadataDto(key, value);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FieldMapping getField() {
        return field;
    }

    public void setField(FieldMapping field) {
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
}
