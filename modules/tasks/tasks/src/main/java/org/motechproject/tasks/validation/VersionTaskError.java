package org.motechproject.tasks.validation;

public class VersionTaskError extends TaskError {

    public VersionTaskError(String objectName, String field) {
        super(objectName, field, "validation.error.version");
    }

}
