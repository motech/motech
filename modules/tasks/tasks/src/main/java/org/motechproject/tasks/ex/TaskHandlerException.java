package org.motechproject.tasks.ex;

import org.motechproject.tasks.events.constants.TaskFailureCause;

import java.util.Arrays;

public class TaskHandlerException extends Exception {
    private static final long serialVersionUID = -59570513535833829L;

    private final TaskFailureCause failureCause;
    private final String messageKey;
    private final String[] args;

    public TaskHandlerException(TaskFailureCause failureCause, String messageKey) {
        this(failureCause, messageKey, null);
    }

    public TaskHandlerException(TaskFailureCause failureCause, String messageKey, String... args) {
        this(failureCause, messageKey, null, args);
    }

    public TaskHandlerException(TaskFailureCause failureCause, String messageKey, Throwable cause, String... args) {
        super(cause);

        this.failureCause = failureCause;
        this.messageKey = messageKey;
        this.args = args == null ? new String[0] : args;
    }

    public TaskFailureCause getFailureCause() {
        return failureCause;
    }

    @Override
    public String getMessage() {
        return messageKey;
    }

    public String[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }

}