package org.motechproject.tasks.service.impl;

import org.motechproject.tasks.domain.Level;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskStatusMessage;
import org.motechproject.tasks.repository.AllTaskStatusMessages;
import org.motechproject.tasks.service.TaskStatusMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskStatusMessageServiceImpl implements TaskStatusMessageService {
    private AllTaskStatusMessages allTaskStatusMessages;

    @Autowired
    public TaskStatusMessageServiceImpl(AllTaskStatusMessages allTaskStatusMessages) {
        this.allTaskStatusMessages = allTaskStatusMessages;
    }

    @Override
    public void addError(Task task, String message) {
        allTaskStatusMessages.add(new TaskStatusMessage(message, task.getId(), Level.ERROR));
    }

    @Override
    public void addSuccess(Task task) {
        allTaskStatusMessages.add(new TaskStatusMessage("success.ok", task.getId(), Level.SUCCESS));
    }

    @Override
    public void addWarning(Task task) {
        allTaskStatusMessages.add(new TaskStatusMessage("warning.taskDisabled", task.getId(), Level.WARNING));
    }

    @Override
    public List<TaskStatusMessage> errorsFromLastRun(Task task) {
        List<TaskStatusMessage> messages = allTaskStatusMessages.byTaskId(task.getId());
        List<TaskStatusMessage> result = new ArrayList<>(messages.size());

        for (int i = messages.size() - 1; i >= 0; --i) {
            TaskStatusMessage msg = messages.get(i);

            if (msg.getLevel() != Level.ERROR) {
                break;
            }

            result.add(msg);
        }

        return result;
    }
}
