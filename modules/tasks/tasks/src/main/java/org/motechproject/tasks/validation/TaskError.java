package org.motechproject.tasks.validation;

public abstract class TaskError {
    private final String objectName;
    private final String field;
    private final String message;
    private final String type;

    protected TaskError(String objectName, String field, String message) {
        this.objectName = objectName;
        this.field = field;
        this.message = message;
        this.type = this.getClass().getSimpleName();
    }

    public String getObjectName() {
        return objectName;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }
}
