package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents single entity instance (used in jqGrid)
 */
public class HistoryRecord {
    private String id;
    private String instanceId;
    private List<FieldRecord> fields;

    public HistoryRecord(String id, String instanceId, List<FieldRecord> fields) {
        this.id = id;
        this.instanceId = instanceId;
        this.fields = fields;
    }

    public HistoryRecord() {
        fields = new ArrayList<>();
    }

    public List<FieldRecord> getFields() {
        return fields;
    }

    public void setFields(List<FieldRecord> fields) {
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
