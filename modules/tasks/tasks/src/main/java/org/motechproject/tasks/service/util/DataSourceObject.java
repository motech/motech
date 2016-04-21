package org.motechproject.tasks.service.util;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * DataSourceObject is the result of a {@link org.motechproject.commons.api.DataProvider} lookup.
 */
public class DataSourceObject {

    private String objectId;
    private Object objectValue;
    private boolean failIfNotFound;
    private boolean nullWarningPublished;

    /**
     * Class constructor.
     *
     * @param objectId  the object id
     * @param objectValue  the object value
     * @param failIfNotFound  defines if the task should fail if object wasn't found
     */
    public DataSourceObject(String objectId, Object objectValue, boolean failIfNotFound) {
        this.objectId = objectId;
        this.objectValue = objectValue;
        this.failIfNotFound = failIfNotFound;
        this.nullWarningPublished = false;
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

    public boolean isNullWarningPublished() { return nullWarningPublished; }

    public void setNullWarningPublished(boolean nullWarningPublished) { this.nullWarningPublished = nullWarningPublished; }

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
