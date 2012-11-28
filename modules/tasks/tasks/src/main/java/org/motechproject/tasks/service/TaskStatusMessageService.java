package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskStatusMessage;

import java.util.List;

public interface TaskStatusMessageService {

    void addError(Task task, String message);

    void addSuccess(Task task);

    void addWarning(Task task);

    List<TaskStatusMessage> errorsFromLastRun(Task task);

}
