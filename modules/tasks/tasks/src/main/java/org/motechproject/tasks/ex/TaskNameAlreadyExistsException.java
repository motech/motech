package org.motechproject.tasks.ex;

/**
 * Thrown when attempting to save task with already existing task name.
 */
public class TaskNameAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 9084172986189170522L;

    /**
     * Exception constructor.
     *
     * @param taskName the task name
     */
    public TaskNameAlreadyExistsException(String taskName) {
        super("Task with name " + taskName + " already exists");
    }
}