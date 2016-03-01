package org.motechproject.tasks.exception;

import org.motechproject.tasks.constants.TaskFailureCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Thrown when there were problems while handling a task.
 */
public class TaskHandlerException extends Exception {

    private static final long serialVersionUID = -59570513535833829L;

    private final TaskFailureCause failureCause;
    private final String messageKey;
    private final List<String> args;

    /**
     * Exception constructor.
     *
     * @param failureCause  the failure cause
     * @param messageKey  the message key
     */
    public TaskHandlerException(TaskFailureCause failureCause, String messageKey) {
        this(failureCause, messageKey, new String[0]);
    }

    /**
     * Exception constructor.
     *
     * @param failureCause  the failure cause
     * @param messageKey  the message key
     * @param args  the arguments
     */
    public TaskHandlerException(TaskFailureCause failureCause, String messageKey, String... args) {
        this(failureCause, messageKey, null, args);
    }

    /**
     * Exception constructor.
     *
     * @param failureCause  the failure cause
     * @param messageKey  the message key
     * @param cause  the cause of the failure
     * @param args  the arguments
     */
    public TaskHandlerException(TaskFailureCause failureCause, String messageKey, Throwable cause, String... args) {
        super(cause);

        this.failureCause = failureCause;
        this.messageKey = messageKey;
        this.args = new ArrayList<>(Arrays.asList(args));
    }

    public TaskFailureCause getFailureCause() {
        return failureCause;
    }

    @Override
    public String getMessage() {
        return messageKey;
    }

    public List<String> getArgs() {
        return args;
    }
}
