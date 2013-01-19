package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.ex.TaskException;

import java.util.List;

public interface TaskActivityService {

    @Deprecated
    void addError(Task task, String message);

    void addError(Task task, TaskException e);

    void addSuccess(Task task);

    void addWarning(Task task);

    void addWarning(Task task, String key, String value);

    List<TaskActivity> errorsFromLastRun(Task task);

    void deleteActivitiesForTask(String taskId);

    List<TaskActivity> getAllActivities();

    List<TaskActivity> getTaskActivities(String taskId);
}
