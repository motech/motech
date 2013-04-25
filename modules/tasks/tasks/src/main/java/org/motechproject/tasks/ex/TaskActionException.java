package org.motechproject.tasks.ex;

public class TaskActionException extends TaskException {
    private static final long serialVersionUID = -8303771366773271162L;

    public TaskActionException(String messageKey) {
        super(messageKey);
    }

    public TaskActionException(String messageKey, Throwable cause, String... fields) {
        super(messageKey, cause, fields);
    }
}
