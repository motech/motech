package org.motechproject.mds.web.domain;

import java.util.List;

/**
 * Represents single entity instance
 */
public class PreviousRecord {
    private Long id;
    private Long historyId;
    private List<FieldRecord> fields;

    public PreviousRecord(Long id, Long historyId, List<FieldRecord> fields) {
        this.id = id;
        this.historyId = historyId;
        this.fields = fields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public List<FieldRecord> getFields() {
        return fields;
    }

    public void setFields(List<FieldRecord> fields) {
        this.fields = fields;
    }
}
