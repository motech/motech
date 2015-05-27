package org.motechproject.tasks.web;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.tasks.constants.TasksRoles;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private static final String JSON_NAME_FIELD = "name";

    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskActivityService activityService;

    /**
     * Returns the list of all tasks.
     *
     * @return  the list of all tasks
     */
    @PreAuthorize(TasksRoles.MANAGE_TASKS)
    @RequestMapping(value = "/task", method = RequestMethod.GET)
    @ResponseBody
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * Imports the task from the given file. The file must be specified as the "jsonFile" parameter in the form body and
     * must be JSON file containing valid task definitions.
     *
     * @param jsonFile  the file to import task from, not null
     * @throws IOException  when there were problems while reading file
     */
    @RequestMapping(value = "/task/import", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void importTask(@RequestParam(value = "jsonFile") MultipartFile jsonFile)
            throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(jsonFile.getInputStream(), writer);

        taskService.importTask(writer.toString());
    }

    /**
     * Returns the task with the given ID.
     *
     * @param taskId  the ID of the task, null returns null
     * @return  the task with given ID, null if not found
     */
    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.GET)
    @ResponseBody
    public Task getTask(@PathVariable Long taskId) {
        return taskService.getTask(taskId);
    }

    /**
     * Updates the task with the given ID. If ID isn't specified in passed task nothing will happen.
     *
     * @param task  the task to be saved, not null
     */
    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void saveTask(@RequestBody Task task) {
        if (task.getId() != null) {
            taskService.save(task);
        }
    }

    /**
     * Deletes the task with the given ID.
     *
     * @param taskId  the ID of the task
     */
    @RequestMapping(value = "/task/{taskId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        activityService.deleteActivitiesForTask(taskId);
    }

    /**
     * Exports the task with the given ID as JSON format file.
     *
     * @param taskId  the ID of the task
     * @param response  the HTTP response
     * @throws IOException  when there were problems while creating response file
     */
    @RequestMapping(value = "/task/{taskId}/export", method = RequestMethod.GET)
    public void exportTask(@PathVariable Long taskId, HttpServletResponse response)
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

    /**
     * Creates new task from the request body.
     *
     * @param task  the task to be saved, not null
     */
    @RequestMapping(value = "/task/save", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody Task task) {
            taskService.save(task);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Set<TaskError> handleException(ValidationException e) throws IOException {
        LOGGER.error("User task did not pass validation", e);
        return e.getTaskErrors();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) throws IOException {
        LOGGER.error("Exception when using the task UI", e);
        return e.getMessage();
    }
}
