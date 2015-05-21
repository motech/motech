package org.motechproject.tasks.web;

import org.motechproject.tasks.domain.TaskActivity;
import org.motechproject.tasks.service.TaskActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Controller for managing activities.
 */
@Controller
public class ActivityController {

    private TaskActivityService activityService;

    /**
     * Controller constructor.
     *
     * @param activityService  the activity service, not null
     */
    @Autowired
    public ActivityController(final TaskActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Returns the list of all activities.
     *
     * @return  the list of activities
     */
    @RequestMapping(value = "/activity", method = RequestMethod.GET)
    @ResponseBody
    public List<TaskActivity> getAllActivities() {
        return activityService.getAllActivities();
    }

    /**
     * Returns the list of activities for task with the given ID.
     *
     * @param taskId  the ID of the task
     * @return  the list of activities
     */
    @RequestMapping(value = "/activity/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public List<TaskActivity> getTaskActivities(@PathVariable Long taskId) {
        return activityService.getTaskActivities(taskId);
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
}
