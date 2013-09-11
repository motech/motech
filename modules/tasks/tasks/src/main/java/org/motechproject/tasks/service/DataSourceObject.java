package org.motechproject.tasks.service;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * DataSourceObject is the result of a {@link org.motechproject.commons.api.DataProvider} lookup.
 */
public class DataSourceObject {

    private String objectId;
    private Object objectValue;
    private boolean failIfNotFound;

    public DataSourceObject(String objectId, Object objectValue, boolean failIfNotFound) {
        this.objectId = objectId;
        this.objectValue = objectValue;
        this.failIfNotFound = failIfNotFound;
    }

    public String getObjectId() {
        return objectId;
    }

    public Object getObjectValue() {
        return objectValue;
    }

    public boolean isFailIfNotFound() {
        return failIfNotFound;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DataSourceObject)) {
            return false;
        }
        return new EqualsBuilder().append(this.objectId, ((DataSourceObject) o).objectId).isEquals();
    }

    @Override
    public int hashCode() {
        return objectId.hashCode();
    }
}
