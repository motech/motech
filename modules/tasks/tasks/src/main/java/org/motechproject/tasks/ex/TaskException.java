package org.motechproject.tasks.ex;

public class TaskException extends Exception {
    private String messageKey;
    private String field;

    public TaskException(String messageKey) {
        this(messageKey, null, null);
    }

    public TaskException(String messageKey, Throwable cause) {
        this(messageKey, null, cause);
    }

    public TaskException(String messageKey, String field) {
        this(messageKey, field, null);
    }

    public TaskException(String messageKey, String field, Throwable cause) {
        super(cause);
        this.messageKey = messageKey;
        this.field = field;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getField() {
        return field;
    }
}
