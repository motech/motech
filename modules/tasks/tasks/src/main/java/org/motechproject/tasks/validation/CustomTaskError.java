package org.motechproject.tasks.validation;

public class CustomTaskError extends TaskError {

    public CustomTaskError(String message) {
        this(null, null, message);
    }

    public CustomTaskError(String objectName, String field, String message) {
        super(objectName, field, message);
    }
}
