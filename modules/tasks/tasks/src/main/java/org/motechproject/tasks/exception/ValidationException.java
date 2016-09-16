package org.motechproject.tasks.exception;

import org.motechproject.tasks.dto.TaskErrorDto;


import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Thrown when there were problems while validating
 */
public class ValidationException extends IllegalArgumentException {

    private static final long serialVersionUID = -8282120820802247252L;

    private final String objectType;
    private final Set<TaskErrorDto> taskErrors;

    /**
     * Exception constructor.
     *
     * @param objectType  the type of the object
     * @param taskErrors  the set of errors
     */
    public ValidationException(String objectType, Set<TaskErrorDto> taskErrors) {
        this.objectType = objectType;
        this.taskErrors = taskErrors;
    }

    /**
     * Generates message based on the given errors and type of the object.
     *
     * @return  the message
     */
    @Override
    public String getMessage() {

        ResourceBundle rb = ResourceBundle.getBundle("webapp/messages.messages");
        StringBuilder sb = new StringBuilder();
        sb.append("There were validation errors during saving ").append(objectType).append(":\n");

        for (TaskErrorDto taskError : taskErrors) {
            sb.append(" - ").append(MessageFormat.format(rb.getString(taskError.getMessage()), taskError.getArgs().toArray())).append("\n");
        }

        return sb.toString();
    }

    public Set<TaskErrorDto> getTaskErrors() {
        return taskErrors;
    }
}
