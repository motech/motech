package org.motechproject.tasks.ex;

public class TaskNameAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 9084172986189170522L;

    public TaskNameAlreadyExistsException(String taskName) {
        super("Task with name " + taskName + " already exists");
    }
}