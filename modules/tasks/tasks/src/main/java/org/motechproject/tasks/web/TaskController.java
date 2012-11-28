package org.motechproject.tasks.web;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TaskTriggerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.motechproject.tasks.util.TaskUtil.getSubject;

@Controller
public class TaskController {
    private TaskService taskService;
    private TaskTriggerHandler triggerHandler;

    @Autowired
    public TaskController(TaskService taskService, TaskTriggerHandler triggerHandler) {
        this.taskService = taskService;
        this.triggerHandler = triggerHandler;
    }

    @RequestMapping(value = "/task/save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody Task task) {
        taskService.save(task);
        triggerHandler.registerHandlerFor(getSubject(task.getTrigger()));
    }
}
