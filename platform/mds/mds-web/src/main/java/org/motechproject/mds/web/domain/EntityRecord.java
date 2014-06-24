package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents single entity instance (used in jqGrid)
 */
public class EntityRecord {
    private Long id;
    private Long entitySchemaId;
    private List<FieldRecord> fields;

    public EntityRecord(Long id, Long entitySchemaId, List<FieldRecord> fields) {
        this.id = id;
        this.entitySchemaId = entitySchemaId;
        this.fields = fields;
    }

    public EntityRecord() {
        fields = new ArrayList<>();
    }

    public List<FieldRecord> getFields() {
        return fields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntitySchemaId() {
        return entitySchemaId;
    }

    public void setEntitySchemaId(Long entitySchemaId) {
        this.entitySchemaId = entitySchemaId;
    }

    public void setFields(List<FieldRecord> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EntityRecord that = (EntityRecord) o;

        if (entitySchemaId != null ? !entitySchemaId.equals(that.entitySchemaId) : that.entitySchemaId != null) {
            return false;
        }
        if (fields != null ? !fields.equals(that.fields) : that.fields != null) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (entitySchemaId != null ? entitySchemaId.hashCode() : 0);
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }
}
