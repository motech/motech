package org.motechproject.mds.web.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents single entity instance (used in jqGrid)
 */
public class EntityRecord extends BasicEntityRecord {
    private Long entitySchemaId;

    public EntityRecord() {
        this(null, null, new ArrayList<FieldRecord>());
    }

    public EntityRecord(Long id, Long entitySchemaId, List<FieldRecord> fields) {
        super(id, fields);
        this.entitySchemaId = entitySchemaId;
    }

    public Long getEntitySchemaId() {
        return entitySchemaId;
    }

    public void setEntitySchemaId(Long entitySchemaId) {
        this.entitySchemaId = entitySchemaId;
    }

    @Override
    public List<FieldRecord> getFields() {
        return (List<FieldRecord>) super.getFields();
    }

    @JsonProperty("fields")
    public void setFieldRecords(List<FieldRecord> fieldRecords) {
        super.setFields(fieldRecords);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityRecord)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        EntityRecord that = (EntityRecord) o;
        return Objects.equals(entitySchemaId, that.entitySchemaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), entitySchemaId);
    }
}
