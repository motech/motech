package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;

import java.util.List;

public interface TaskActivityService {

    void addError(Task task, String message);

    void addSuccess(Task task);

    void addWarning(Task task);

    List<TaskActivity> errorsFromLastRun(Task task);

    void deleteActivitiesForTask(String taskId);

    List<TaskActivity> getAllActivities();
}
