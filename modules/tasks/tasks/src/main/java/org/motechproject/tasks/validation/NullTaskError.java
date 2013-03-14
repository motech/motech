package org.motechproject.tasks.validation;

public class NullTaskError extends TaskError {

    public NullTaskError(String objectName, String field) {
        super(objectName, field, "validation.error.null");
    }

}
