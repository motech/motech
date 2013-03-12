package org.motechproject.tasks.ex;

import org.motechproject.tasks.validation.TaskError;

import java.util.List;

public class ValidationException extends IllegalArgumentException {
    private static final long serialVersionUID = -8282120820802247252L;

    private final List<TaskError> taskErrors;

    public ValidationException(List<TaskError> taskErrors) {
        this.taskErrors = taskErrors;
    }

    public List<TaskError> getTaskErrors() {
        return taskErrors;
    }
}
