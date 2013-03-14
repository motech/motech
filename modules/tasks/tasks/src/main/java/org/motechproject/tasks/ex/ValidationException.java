package org.motechproject.tasks.ex;

import org.motechproject.tasks.validation.TaskError;

import java.util.List;

public class ValidationException extends IllegalArgumentException {
    private static final long serialVersionUID = -8282120820802247252L;

    private final String objectType;
    private final List<TaskError> taskErrors;

    public ValidationException(String objectType, List<TaskError> taskErrors) {
        this.objectType = objectType;
        this.taskErrors = taskErrors;
    }

    public List<TaskError> getTaskErrors() {
        return taskErrors;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("There were validation errors during saving ").append(objectType).append(":\n");

        for (TaskError taskError : taskErrors) {
            sb.append(" - ").append(taskError).append("\n");
        }

        return sb.toString();
    }
}
