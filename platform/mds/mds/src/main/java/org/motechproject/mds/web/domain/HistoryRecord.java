package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents single entity instance (used in jqGrid)
 */
public class HistoryRecord {
    private Long id;
    private Long instanceId;
    private List<FieldRecord> fields;

    public HistoryRecord(Long id, Long instanceId, List<FieldRecord> fields) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }
}
