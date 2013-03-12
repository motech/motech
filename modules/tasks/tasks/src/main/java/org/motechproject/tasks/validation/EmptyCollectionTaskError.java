package org.motechproject.tasks.validation;

public class EmptyCollectionTaskError extends TaskError {

    public EmptyCollectionTaskError(String objectName, String field) {
        super(objectName, field, "validation.error.emptyCollection");
    }

}
