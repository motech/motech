package org.motechproject.tasks.service.util;

public class PostActionParameterObject {

    private String objectId;
    private Object objectValue;
    private boolean failIfNotFound;
    private boolean nullWarningPublished;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Object getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(Object objectValue) {
        this.objectValue = objectValue;
    }

    public boolean isFailIfNotFound() {
        return failIfNotFound;
    }

    public void setFailIfNotFound(boolean failIfNotFound) {
        this.failIfNotFound = failIfNotFound;
    }

    public boolean isNullWarningPublished() {
        return nullWarningPublished;
    }

    public void setNullWarningPublished(boolean nullWarningPublished) {
        this.nullWarningPublished = nullWarningPublished;
    }

    public PostActionParameterObject(String objectId, Object objectValue, boolean failIfNotFound) {
        this.objectId = objectId;
        this.objectValue = objectValue;
        this.failIfNotFound = failIfNotFound;
    }
}
