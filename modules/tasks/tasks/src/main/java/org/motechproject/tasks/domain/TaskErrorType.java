package org.motechproject.tasks.domain;

public enum TaskErrorType {
    BLANK("validation.error.blank"),
    EMPTY_COLLECTION("validation.error.emptyCollection"),
    NULL("validation.error.null"),
    VERSION("validation.error.version");

    private String message;

    private TaskErrorType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
