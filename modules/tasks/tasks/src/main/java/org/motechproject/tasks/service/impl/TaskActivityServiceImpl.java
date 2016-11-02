package org.motechproject.tasks.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.domain.mds.task.TaskExecutionProgress;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.repository.TaskActivitiesDataService;
import org.motechproject.tasks.service.TaskActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TaskActivityServiceImpl implements TaskActivityService {

    private static final String TASK_IN_PROGRESS = "task.inProgress";
    private static final String TASK_SUCCEEDED = "task.success.ok";
    private static final String TASK_DISABLED = "task.warning.taskDisabled";
    private static final String TASK_FILTERED = "task.filtered";

    private TaskActivitiesDataService taskActivitiesDataService;

    @Autowired
    public TaskActivityServiceImpl(TaskActivitiesDataService taskActivitiesDataService) {
        this.taskActivitiesDataService = taskActivitiesDataService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public long addTaskStarted(Task task, Map<String, Object> parameters) {
        int totalActions = task.getActions().size();
        TaskActivity activity = taskActivitiesDataService.create(
                new TaskActivity(TASK_IN_PROGRESS, Arrays.asList("0", String.valueOf(totalActions)), task.getId(),
                        TaskActivityType.IN_PROGRESS, null, parameters, new TaskExecutionProgress(totalActions)));
        return activity.getId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addTaskFiltered(Long activityId) {
        TaskActivity activity = taskActivitiesDataService.findById(activityId);

        activity.setActivityType(TaskActivityType.FILTERED);
        activity.setMessage(TASK_FILTERED);
        taskActivitiesDataService.update(activity);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean addSuccessfulExecution(Long activityId) {
        TaskActivity activity = taskActivitiesDataService.findById(activityId);
        if (activity == null) {
            return false;
        }

        TaskExecutionProgress progress = activity.getTaskExecutionProgress();
        progress.addSuccess();
        boolean taskFinished = progress.getActionsSucceeded() == progress.getTotalActions();

        if (taskFinished) {
            activity.setActivityType(TaskActivityType.SUCCESS);
            activity.setMessage(TASK_SUCCEEDED);
            activity.getFields().clear();
        }

        updateTaskInProgressMessage(activity);
        taskActivitiesDataService.update(activity);

        return taskFinished;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addFailedExecution(Long activityId, Throwable e) {
        TaskActivity activity = taskActivitiesDataService.findById(activityId);

        if (activity == null){
            return;
        }

        if (activity.getActivityType() != TaskActivityType.ERROR) {
            activity.setMessage(e.getMessage());
            activity.setActivityType(TaskActivityType.ERROR);

            if (e instanceof TaskHandlerException) {
                activity.setFields(((TaskHandlerException) e).getArgs());
            }

            activity.setStackTraceElement(ExceptionUtils.getStackTrace(e));
            taskActivitiesDataService.update(activity);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addTaskDisabledWarning(Task task) {
        taskActivitiesDataService.create(new TaskActivity(TASK_DISABLED, task.getId(), TaskActivityType.WARNING));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addWarning(Task task, String key, String field) {
        taskActivitiesDataService.create(new TaskActivity(key, field, task.getId(), TaskActivityType.WARNING));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addWarningWithException(Task task, String key, String field, Exception e) {
        taskActivitiesDataService.create(new TaskActivity(key, new ArrayList<>(Arrays.asList(field)),
                task.getId(), TaskActivityType.WARNING, ExceptionUtils.getStackTrace(e.getCause())));
    }

    @Override
    @Transactional
    public void deleteActivitiesForTask(Long taskId) {
        for (TaskActivity msg : taskActivitiesDataService.byTask(taskId)) {
            taskActivitiesDataService.delete(msg);
        }
    }

    @Override
    @Transactional
    public TaskActivity getTaskActivityById(Long activityId) {
        return taskActivitiesDataService.findById(activityId);
    }

    @Override
    @Transactional
    public List<TaskActivity> getLatestActivities() {
        return taskActivitiesDataService.retrieveAll(new QueryParams(1, 10, new Order("date", Order.Direction.DESC)));
    }

    @Override
    @Transactional
    public List<TaskActivity> getAllActivities(Set<TaskActivityType> activityTypes, QueryParams queryParams) {
        return taskActivitiesDataService.byActivityTypes(activityTypes, queryParams);
    }

    @Override
    @Transactional
    public List<TaskActivity> getAllActivities(Set<TaskActivityType> activityTypes, Range<DateTime> dateRange,
                                               QueryParams queryParams) {
        return taskActivitiesDataService.byActivityTypesAndDate(activityTypes, dateRange, queryParams);
    }

    @Override
    @Transactional
    public List<TaskActivity> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypes, QueryParams queryParams) {
        return taskActivitiesDataService.byTaskAndActivityTypes(taskId, activityTypes, queryParams);
    }

    @Override
    @Transactional
    public List<TaskActivity> getTaskActivities(Long taskId, Set<TaskActivityType> activityTypes, Range<DateTime> dateRange,
                                                QueryParams queryParams) {
        return taskActivitiesDataService.byTaskAndActivityTypesAndDate(taskId, activityTypes, dateRange, queryParams);
    }

    @Override
    @Transactional
    public long getTaskActivitiesCount(Long taskId, Set<TaskActivityType> activityTypes) {
        return taskActivitiesDataService.countByTaskAndActivityTypes(taskId, activityTypes);
    }

    @Override
    @Transactional
    public long getTaskActivitiesCount(Long taskId, TaskActivityType type) {
        return taskActivitiesDataService.countByTaskAndActivityTypes(taskId, new HashSet<>(Arrays.asList(type)));
    }

    @Override
    @Transactional
    public long getTaskActivitiesCount(Long taskId, Set<TaskActivityType> activityTypes, Range<DateTime> dateRange) {
        return taskActivitiesDataService.countByTaskAndActivityTypesAndDate(taskId, activityTypes, dateRange);
    }

    @Override
    @Transactional
    public long getAllTaskActivitiesCount(Set<TaskActivityType> activityTypes) {
        return taskActivitiesDataService.countByActivityTypes(activityTypes);
    }

    @Override
    @Transactional
    public long getAllTaskActivitiesCount(Set<TaskActivityType> activityTypes, Range<DateTime> dateRange) {
        return taskActivitiesDataService.countByActivityTypesAndDate(activityTypes, dateRange);
    }

    private void updateTaskInProgressMessage(TaskActivity activity) {
        if (TASK_IN_PROGRESS.equals(activity.getMessage())) {
            activity.getFields().set(0, String.valueOf(activity.getTaskExecutionProgress().getActionsSucceeded()));
        }
    }
}