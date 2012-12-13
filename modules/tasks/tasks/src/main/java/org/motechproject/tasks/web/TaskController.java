package org.motechproject.tasks.web;

import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TaskStatusMessageService;
import org.motechproject.tasks.service.TaskTriggerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.motechproject.tasks.util.TaskUtil.getSubject;

@Controller
public class TaskController {
    private TaskService taskService;
    private TaskStatusMessageService messageService;
    private TaskTriggerHandler triggerHandler;

    @Autowired
    public TaskController(TaskService taskService, TaskStatusMessageService messageService, TaskTriggerHandler triggerHandler) {
        this.taskService = taskService;
        this.messageService = messageService;
        this.triggerHandler = triggerHandler;
    }

    @RequestMapping(value = "/task", method = RequestMethod.GET)
    @ResponseBody
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public Task getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        messageService.deleteMessages(taskId);
    }

    @RequestMapping(value = "/task/save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody Task task) {
        taskService.save(task);
        triggerHandler.registerHandlerFor(getSubject(task.getTrigger()));
    }
}
