package org.motechproject.tasks.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.mds.task.TaskActivityType;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.repository.TaskActivitiesDataService;
import org.motechproject.tasks.service.TaskActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TaskActivityServiceImpl implements TaskActivityService {

    private TaskActivitiesDataService taskActivitiesDataService;

    @Autowired
    public TaskActivityServiceImpl(TaskActivitiesDataService taskActivitiesDataService) {
        this.taskActivitiesDataService = taskActivitiesDataService;
    }

    @Override
    public void addError(Task task, TaskHandlerException e, Map<String, Object> parameters) {
        taskActivitiesDataService.create(new TaskActivity(e.getMessage(), e.getArgs(), task.getId(),
                TaskActivityType.ERROR, ExceptionUtils.getStackTrace(e), parameters));
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
    public void deleteActivitiesForTask(Long taskId) {
        for (TaskActivity msg : taskActivitiesDataService.byTask(taskId)) {
            taskActivitiesDataService.delete(msg);
        }
    }

    @Override
    public TaskActivity getTaskActivityById(Long activityId) {
        return taskActivitiesDataService.findById(activityId);
    }

    @Override
    public List<TaskActivity> getLatestActivities() {
        return taskActivitiesDataService.retrieveAll(new QueryParams(1, 10, new Order("date", Order.Direction.DESC)));
    }

    @Override
    public List<TaskActivity> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypes, QueryParams queryParams) {
        return taskActivitiesDataService.byTaskAndActivityTypes(taskId, activityTypes, queryParams);
    }

    @Override
    public long getTaskActivitiesCount(Long taskId, Set<TaskActivityType> activityTypes) {
        return taskActivitiesDataService.countByTaskAndActivityTypes(taskId, activityTypes);
    }

    @Override
    public long getTaskActivitiesCount(Long taskId, TaskActivityType type) {
        return taskActivitiesDataService.countByTaskAndActivityTypes(taskId, new HashSet<>(Arrays.asList(type)));
    }
}