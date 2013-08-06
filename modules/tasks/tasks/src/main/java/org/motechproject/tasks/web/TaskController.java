package org.motechproject.tasks.web;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TriggerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static org.apache.commons.lang.CharEncoding.UTF_8;
import static org.codehaus.jackson.map.SerializationConfig.Feature.INDENT_OUTPUT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST API for managing tasks.
 */
@Controller
public class TaskController {
    private static final String JSON_NAME_FIELD = "name";

    private TaskService taskService;
    private TaskActivityService activityService;
    private TriggerHandler triggerHandler;

    @Autowired
    public TaskController(TaskService taskService, TaskActivityService activityService,
                          TriggerHandler triggerHandler) {
        this.taskService = taskService;
        this.activityService = activityService;
        this.triggerHandler = triggerHandler;
    }

    @RequestMapping(value = "/task", method = RequestMethod.GET)
    @ResponseBody
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @RequestMapping(value = "/task/import", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void importTask(@RequestParam(value = "jsonFile") MultipartFile jsonFile)
            throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(jsonFile.getInputStream(), writer);

        taskService.importTask(writer.toString());
    }

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public Task getTask(@PathVariable String taskId) {
        return taskService.getTask(taskId);
    }

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveTask(@RequestBody Task task) {
        if (task.getId() != null) {
            taskService.save(task);
        }
    }

    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable String taskId) {
        taskService.deleteTask(taskId);
        activityService.deleteActivitiesForTask(taskId);
    }

    @RequestMapping(value = "/task/{taskId}/export", method = RequestMethod.GET)
    public void exportTask(@PathVariable String taskId, HttpServletResponse response)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(INDENT_OUTPUT);

        String json = taskService.exportTask(taskId);
        JsonNode node = mapper.readTree(json);

        String fileName = node.has(JSON_NAME_FIELD)
                ? node.get(JSON_NAME_FIELD).getTextValue()
                : "task";

        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8);
        response.setHeader(
                "Content-Disposition",
                format("attachment; filename=%s.json", encode(fileName, UTF_8))
        );

        response.getWriter().write(mapper.writeValueAsString(node));
    }

    @RequestMapping(value = "/task/save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody Task task) {
        taskService.save(task);
        triggerHandler.registerHandlerFor(task.getTrigger().getSubject());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Set<TaskError> handleException(ValidationException e) throws IOException {
        return e.getTaskErrors();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        return e.getMessage();
    }
}
