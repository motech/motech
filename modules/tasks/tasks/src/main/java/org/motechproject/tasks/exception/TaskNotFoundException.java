package org.motechproject.tasks.exception;

import static java.lang.String.format;

/**
 * Thrown when task with given ID doesn't exists.
 */
public class TaskNotFoundException extends IllegalArgumentException {

    /**
     * Exception constructor.
     *
     * @param taskId  the task ID
     */
    public TaskNotFoundException(Long taskId) {
        super(format("Not found task with ID: %s", taskId));
    }
}
