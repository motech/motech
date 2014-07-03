package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;

import java.io.IOException;
import java.util.List;

public interface TaskService {

    void save(final Task task);

    ActionEvent getActionEventFor(TaskActionInformation taskActionInformation) throws ActionNotFoundException;

    List<Task> getAllTasks();

    List<Task> findTasksForTrigger(final TriggerEvent trigger);

    List<Task> findTasksForTriggerSubject(final String subject);

    List<Task> findTasksDependentOnModule(String moduleName);

    TriggerEvent findTrigger(String subject) throws TriggerNotFoundException;

    Task getTask(Long taskId);

    void deleteTask(Long taskId);

    String exportTask(Long taskId);

    void importTask(String json) throws IOException;
}
