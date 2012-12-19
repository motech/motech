package org.motechproject.tasks.service.impl;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.domain.TaskActivityType;
import org.motechproject.tasks.repository.AllTaskActivities;
import org.motechproject.tasks.service.TaskActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskActivityServiceImpl implements TaskActivityService {
    private AllTaskActivities allTaskActivities;

    @Autowired
    public TaskActivityServiceImpl(AllTaskActivities allTaskActivities) {
        this.allTaskActivities = allTaskActivities;
    }

    @Override
    public void addError(Task task, String message) {
        allTaskActivities.add(new TaskActivity(message, task.getId(), TaskActivityType.ERROR));
    }

    @Override
    public void addSuccess(Task task) {
        allTaskActivities.add(new TaskActivity("success.ok", task.getId(), TaskActivityType.SUCCESS));
    }

    @Override
    public void addWarning(Task task) {
        allTaskActivities.add(new TaskActivity("warning.taskDisabled", task.getId(), TaskActivityType.WARNING));
    }

    @Override
    public List<TaskActivity> errorsFromLastRun(Task task) {
        List<TaskActivity> messages = allTaskActivities.byTaskId(task.getId());
        List<TaskActivity> result = new ArrayList<>(messages.size());

        for (int i = messages.size() - 1; i >= 0; --i) {
            TaskActivity msg = messages.get(i);

            if (msg.getActivityType() != TaskActivityType.ERROR) {
                break;
            }

            result.add(msg);
        }

        return result;
    }

    @Override
    public void deleteActivitiesForTask(String taskId) {
        for (TaskActivity msg : allTaskActivities.byTaskId(taskId)) {
            allTaskActivities.remove(msg);
        }
    }

    @Override
    public List<TaskActivity> getAllActivities() {
        List<TaskActivity> messages = allTaskActivities.getAll();

        Collections.sort(messages, new Comparator<TaskActivity>() {
            @Override
            public int compare(TaskActivity o1, TaskActivity o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        return messages;
    }
}
