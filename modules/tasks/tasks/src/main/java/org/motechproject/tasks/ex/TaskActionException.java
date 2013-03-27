package org.motechproject.tasks.ex;

public class TaskActionException extends TaskException {

    public TaskActionException(String messageKey) {
        super(messageKey);
    }

    public TaskActionException(String messageKey, Throwable cause) {
        super(messageKey, cause);
    }

    public TaskActionException(String messageKey, String... fields) {
        super(messageKey, fields);
    }

    public TaskActionException(String messageKey, Throwable cause, String... fields) {
        super(messageKey, cause, fields);
    }
}
