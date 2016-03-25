package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents simplified revision of an entity instance (used in grids)
 */
public class BasicHistoryRecord {

    private Long id;
    private Long instanceId;
    private boolean revertable;
    private List<? extends BasicFieldRecord> fields;

    public BasicHistoryRecord(Long id, Long instanceId, boolean revertable, List<? extends BasicFieldRecord> fields) {
        this.id = id;
        this.instanceId = instanceId;
        this.revertable = revertable;
        this.fields = fields;
    }

    public BasicHistoryRecord() {
        fields = new ArrayList<>();
    }

    public List<? extends BasicFieldRecord> getFields() {
        return fields;
    }

    public void setFields(List<? extends BasicFieldRecord> fields) {
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

    public boolean isRevertable() {
        return revertable;
    }

    public void setRevertable(boolean revertable) {
        this.revertable = revertable;
    }
}
