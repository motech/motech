package org.motechproject.tasks.web;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.dto.TaskActivityDto;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskWebService;
import org.motechproject.tasks.service.TriggerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

/**
 * Controller for managing activities.
 */
@Controller
public class ActivityController {

    private TaskActivityService activityService;
    private TriggerHandler taskTriggerHandler;
    private TaskWebService taskWebService;

    /**
     * Controller constructor.
     *
     * @param activityService  the activity service, not null
     */
    @Autowired
    public ActivityController(final TaskActivityService activityService, TriggerHandler taskTriggerHandler, TaskWebService taskWebService) {
        this.activityService = activityService;
        this.taskTriggerHandler = taskTriggerHandler;
        this.taskWebService = taskWebService;
    }

    /**
     * Returns the list of recent activities.
     *
     * @return  the list of activities
     */
    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    @ResponseBody
    public List<TaskActivityDto> getRecentActivities() {
        return taskWebService.getLatestActivities();
    }

    /**
     * Returns the list of all the task activities.
     *
     * @return the list of activities
     */
    @RequestMapping(value = "/activity/all", method = RequestMethod.GET)
    @ResponseBody
    public TaskActivityRecords getAllActivities(GridSettings settings) {
        if (settings != null) {
            QueryParams params = getParams(settings);

            Set<TaskActivityType> types = settings.getTypesFromString();
            Range<DateTime> dateTimeRange = settings.convertToDateRange(settings.getDateTimeFrom(), settings.getDateTimeTo());
            List<TaskActivityDto> activities;
            long count;
            if (dateTimeRange != null) {
                activities = taskWebService.getAllActivities(types, dateTimeRange, params, settings.isLastExecution());
                count = activityService.getAllTaskActivitiesCount(types, dateTimeRange, settings.isLastExecution());
            } else {
                activities = taskWebService.getAllActivities(types, params, settings.isLastExecution());
                count = activityService.getAllTaskActivitiesCount(types, settings.isLastExecution());
            }
            int totalPages = (int) Math.ceil((double) count / settings.getRows());

            return new TaskActivityRecords(settings.getPage(), totalPages, count, activities);
        } else {
            return null;
        }
    }
    /**
     * Returns the list of activities for task with the given ID.
     *
     * @param taskId  the ID of the task
     * @return  the list of activities
     */
    @RequestMapping(value = "/activity/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public TaskActivityRecords getTaskActivities(@PathVariable Long taskId, GridSettings settings) {
        if (settings != null) {
            QueryParams params = getParams(settings);

            Set<TaskActivityType> types = settings.getTypesFromString();
            Range<DateTime> dateTimeRange = settings.convertToDateRange(settings.getDateTimeFrom(), settings.getDateTimeTo());
            List<TaskActivityDto> activities;
            long count;
            if (dateTimeRange != null) {
                activities = taskWebService.getTaskActivities(taskId, types, dateTimeRange, params, settings.isLastExecution());
                count = activityService.getTaskActivitiesCount(taskId, types, dateTimeRange, settings.isLastExecution());
            } else {
                activities = taskWebService.getTaskActivities(taskId, types, params, settings.isLastExecution());
                count = activityService.getTaskActivitiesCount(taskId, types, settings.isLastExecution());
            }
            int totalPages = (int) Math.ceil((double) count / settings.getRows());

            return new TaskActivityRecords(settings.getPage(), totalPages, count, activities);
        } else {
            return null;
        }
    }

    /**
     * Returns the count of specified activity types for the task with the given ID.
     *
     * @param taskId  the ID of the task
     * @param activityType the type of activities to count; ERROR, WARNING or SUCCESS only
     * @return  the list of activities
     */
    @RequestMapping(value = "/activity/{taskId}/{activityType}", method = RequestMethod.GET)
    @ResponseBody
    public long getTaskActivityCount(@PathVariable Long taskId, @PathVariable String activityType) {
        TaskActivityType type = TaskActivityType.valueOf(activityType);
        return activityService.getTaskActivitiesCount(taskId, type);
    }

    /**
     * Deletes all activities for task with the given ID.
     *
     * @param taskId  the ID of the task
     */
    @RequestMapping(value = "/activity/{taskId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteActivitiesForTask(@PathVariable Long taskId) {
        activityService.deleteActivitiesForTask(taskId);
    }

    /**
     * Retries task execution for activity with the given ID.
     *
     * @param activityId the ID of activity for which task should be retried
     */
    @RequestMapping(value = "/activity/retry/{activityId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void retryTask(@PathVariable Long activityId) {
        //Retry of the task is run in new thread to avoid blocking UI, so user will be able
        //to continue using MOTECH while retry is running in background. It also allow us to inform
        //user immediately about request successfuly reaching controller.
        Thread retryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                taskTriggerHandler.retryTask(activityId);
            }
        });
        retryThread.start();
    }

    /**
     * Retries task execution for the given list of activities' ids.
     *
     * @param activityIds the list of activity ids that are to be retried
     */
    @RequestMapping(value = "/activity/retryMultiple", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void retryMultipleTasks(@RequestBody List<Long> activityIds) {
        for (Long activityId : activityIds) {
            retryTask(activityId);
        }
    }

    private QueryParams getParams(GridSettings settings) {
        QueryParams result;

        if (StringUtils.isEmpty(settings.getSidx())) {
            result = new QueryParams(settings.getPage(), settings.getRows(), new Order("date", Order.Direction.DESC));
        } else {
            if ("asc".equals(settings.getSord())) {
                result = new QueryParams(settings.getPage(), settings.getRows(), new Order(settings.getSidx(), Order.Direction.ASC));
            } else {
                result = new QueryParams(settings.getPage(), settings.getRows(), new Order(settings.getSidx(), Order.Direction.DESC));
            }
        }

        return result;
    }
}
