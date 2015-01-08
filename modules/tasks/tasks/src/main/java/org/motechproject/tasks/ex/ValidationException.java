package org.motechproject.tasks.ex;

import org.motechproject.tasks.domain.TaskError;


import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Set;

public class ValidationException extends IllegalArgumentException {
    private static final long serialVersionUID = -8282120820802247252L;

    private final String objectType;
    private final Set<TaskError> taskErrors;

    public ValidationException(String objectType, Set<TaskError> taskErrors) {
        this.objectType = objectType;
        this.taskErrors = taskErrors;
    }

    public Set<TaskError> getTaskErrors() {
        return taskErrors;
    }

    @Override
    public String getMessage() {

        ResourceBundle rb = ResourceBundle.getBundle("webapp/messages.messages");
        StringBuilder sb = new StringBuilder();
        sb.append("There were validation errors during saving ").append(objectType).append(":\n");

        for (TaskError taskError : taskErrors) {
            sb.append(" - ").append(MessageFormat.format(rb.getString(taskError.getMessage()), taskError.getArgs().toArray())).append("\n");
        }

        return sb.toString();
    }
}
