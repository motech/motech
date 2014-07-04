package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.ex.TaskHandlerException;

import java.util.List;

public interface TaskActivityService {

    void addError(Task task, TaskHandlerException e);

    void addSuccess(Task task);

    void addWarning(Task task);

    void addWarning(Task task, String key, String value);

    List<TaskActivity> errorsFromLastRun(Task task);

    void deleteActivitiesForTask(Long taskId);

    List<TaskActivity> getAllActivities();

    List<TaskActivity> getTaskActivities(Long taskId);

    void addWarning(Task task, String key, String field, Exception e);
}
