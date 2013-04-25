package org.motechproject.tasks.ex;

public class TaskTriggerException extends TaskException {
    private static final long serialVersionUID = -4596985095010942897L;

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
