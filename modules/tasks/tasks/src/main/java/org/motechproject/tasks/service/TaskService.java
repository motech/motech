package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;

import java.util.List;

public interface TaskService {

    void save(final Task task);

    ActionEvent getActionEventFor(Task task) throws ActionNotFoundException;

    List<Task> getAllTasks();

    List<Task> findTasksForTrigger(final TriggerEvent trigger);

    TriggerEvent findTrigger(String subject) throws TriggerNotFoundException;

    Task getTask(String taskId);

    void deleteTask(String taskId);
}
