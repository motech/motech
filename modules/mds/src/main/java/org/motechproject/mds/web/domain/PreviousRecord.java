package org.motechproject.mds.web.domain;

import java.util.List;

/**
 * Represents single entity instance
 */
public class PreviousRecord {
    private String id;
    private String historyId;
    private List<FieldRecord> fields;

    public PreviousRecord(String id, String historyId, List<FieldRecord> fields) {
        this.id = id;
        this.historyId = historyId;
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHistoryId() {
        return historyId;
    }

    public void setHistoryId(String historyId) {
        this.historyId = historyId;
    }

    public List<FieldRecord> getFields() {
        return fields;
    }

    public void setFields(List<FieldRecord> fields) {
        this.fields = fields;
    }
}
