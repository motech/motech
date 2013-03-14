package org.motechproject.tasks.validation;

public class BlankTaskError extends TaskError {

    public BlankTaskError(String objectName, String field) {
        super(objectName, field, "validation.error.blank");
    }

}
