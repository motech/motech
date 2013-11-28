package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents single entity instance (used in jqGrid)
 */
public class EntityRecord {
    private String id;
    private String entitySchemaId;
    private List<FieldRecord> fields;

    public EntityRecord(String id, String entitySchemaId, List<FieldRecord> fields) {
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntitySchemaId() {
        return entitySchemaId;
    }

    public void setEntitySchemaId(String entitySchemaId) {
        this.entitySchemaId = entitySchemaId;
    }

    public void setFields(List<FieldRecord> fields) {
        this.fields = fields;
    }
}
