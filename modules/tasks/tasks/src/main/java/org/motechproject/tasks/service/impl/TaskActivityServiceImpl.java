package org.motechproject.tasks.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.domain.TaskActivityType;
import org.motechproject.tasks.ex.TaskHandlerException;
import org.motechproject.tasks.repository.TaskActivitiesDataService;
import org.motechproject.tasks.service.TaskActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class TaskActivityServiceImpl implements TaskActivityService {

    private TaskActivitiesDataService taskActivitiesDataService;

    @Autowired
    public TaskActivityServiceImpl(TaskActivitiesDataService taskActivitiesDataService) {
        this.taskActivitiesDataService = taskActivitiesDataService;
    }

    @Override
    public void addError(Task task, TaskHandlerException e) {
        taskActivitiesDataService.create(new TaskActivity(e.getMessage(), e.getArgs(), task.getId(),
                TaskActivityType.ERROR, ExceptionUtils.getStackTrace(e)));
    }

    @Override
    public void addSuccess(Task task) {
        taskActivitiesDataService.create(new TaskActivity("task.success.ok", task.getId(),
                TaskActivityType.SUCCESS));
    }

    @Override
    public void addWarning(Task task) {
        taskActivitiesDataService.create(new TaskActivity("task.warning.taskDisabled", task.getId(),
                TaskActivityType.WARNING));
    }

    @Override
    public void addWarning(Task task, String key, String field) {
        taskActivitiesDataService.create(new TaskActivity(key, field, task.getId(),
                TaskActivityType.WARNING));
    }

    @Override
    public void addWarning(Task task, String key, String field, Exception e) {
        taskActivitiesDataService.create(new TaskActivity(key, new ArrayList<>(Arrays.asList(field)),
                task.getId(), TaskActivityType.WARNING, ExceptionUtils.getStackTrace(e.getCause())));
    }

    @Override
    public List<TaskActivity> errorsFromLastRun(Task task) {
        List<TaskActivity> messages = taskActivitiesDataService.byTask(task.getId());
        Collections.sort(messages);
        List<TaskActivity> result = new ArrayList<>(messages.size());

        for (int i = messages.size() - 1; i >= 0; --i) {
            TaskActivity msg = messages.get(i);

            if ("task.warning.taskDisabled".equals(msg.getMessage()) ||
                    msg.getActivityType() == TaskActivityType.SUCCESS) {
                break;
            }

            result.add(msg);
        }

        return result;
    }

    @Override
    public void deleteActivitiesForTask(Long taskId) {
        for (TaskActivity msg : taskActivitiesDataService.byTask(taskId)) {
            taskActivitiesDataService.delete(msg);
        }
    }

    @Override
    public List<TaskActivity> getAllActivities() {
        return sort(taskActivitiesDataService.retrieveAll());
    }

    @Override
    public List<TaskActivity> getTaskActivities(Long taskId) {
        return sort(taskActivitiesDataService.byTask(taskId));
    }

    private List<TaskActivity> sort(List<TaskActivity> messages) {
        Collections.sort(messages, new Comparator<TaskActivity>() {
            @Override
            public int compare(TaskActivity o1, TaskActivity o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        return messages;
    }
}
