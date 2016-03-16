package org.motechproject.mds.web.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents simplified entity instance (used in grids)
 */
public class BasicEntityRecord {
    private Long id;
    private List<? extends BasicFieldRecord> fields;

    public BasicEntityRecord() {
        this(null, new ArrayList<BasicFieldRecord>());
    }

    public BasicEntityRecord(Long id, List<? extends BasicFieldRecord> fields) {
        this.id = id;
        this.fields = fields;
    }

    public void setFields(List<? extends BasicFieldRecord> fields) {
        this.fields = fields;
    }

    public List<? extends BasicFieldRecord> getFields() {
        return fields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BasicEntityRecord that = (BasicEntityRecord) o;

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
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }
}
