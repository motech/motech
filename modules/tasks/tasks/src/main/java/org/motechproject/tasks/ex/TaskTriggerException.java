package org.motechproject.tasks.ex;

public class TaskTriggerException extends TaskException {

    public TaskTriggerException(String messageKey) {
        super(messageKey);
    }

    public TaskTriggerException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }

    public TaskTriggerException(String messageKey, String... fields) {
        super(messageKey, fields);
    }

    public TaskTriggerException(String messageKey, Throwable cause, String... fields) {
        super(messageKey, cause, fields);
    }
}
