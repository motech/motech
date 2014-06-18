package org.motechproject.tasks.ex;

import org.motechproject.tasks.events.constants.TaskFailureCause;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskHandlerException extends Exception {
    private static final long serialVersionUID = -59570513535833829L;

    private final TaskFailureCause failureCause;
    private final String messageKey;
    private final List<String> args;

    public TaskHandlerException(TaskFailureCause failureCause, String messageKey) {
        this(failureCause, messageKey, new String[0]);
    }

    public TaskHandlerException(TaskFailureCause failureCause, String messageKey, String... args) {
        this(failureCause, messageKey, null, args);
    }

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
