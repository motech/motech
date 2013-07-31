package org.motechproject.tasks.web;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TaskTriggerHandler;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.apache.commons.lang.CharEncoding.UTF_8;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class TaskControllerTest {
    private static final String TASK_ID = "12345";

    @Mock
    TaskService taskService;

    @Mock
    TaskActivityService messageService;

    @Mock
    EventListenerRegistryService eventListenerRegistryService;

    @Mock
    HttpServletResponse response;

    @Mock
    PrintWriter printWriter;

    @Mock
    MultipartFile file;

    TaskTriggerHandler taskTriggerHandler;

    TaskController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        taskTriggerHandler = new TaskTriggerHandler(taskService, null, eventListenerRegistryService, null, null);
        controller = new TaskController(taskService, messageService, taskTriggerHandler);
    }

    @Test
    public void shouldGetAllTasks() {
        TaskActionInformation action = new TaskActionInformation("receive", "action1", "action", "0.15", "receive", new HashMap<String, String>());
        TaskEventInformation trigger = new TaskEventInformation("send", "trigger1", "trigger", "0.16", "send");

        List<Task> expected = new ArrayList<>();
        expected.add(new Task("name", trigger, asList(action)));
        expected.add(new Task("name", trigger, asList(action)));
        expected.add(new Task("name", trigger, asList(action)));

        when(taskService.getAllTasks()).thenReturn(expected);

        List<Task> actual = controller.getAllTasks();

        verify(taskService, times(2)).getAllTasks();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTaskWithId() {
        Task expected = new Task();
        expected.setId(TASK_ID);

        when(taskService.getTask(expected.getId())).thenReturn(expected);

        Task actual = controller.getTask(expected.getId());

        verify(taskService).getTask(expected.getId());

        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
    }

    @Test
    public void shouldDeleteTaskAndHistory() {
        controller.deleteTask(TASK_ID);

        verify(taskService).deleteTask(TASK_ID);
        verify(messageService).deleteActivitiesForTask(TASK_ID);
    }

    @Test
    public void shouldSaveExistingTask() {
        Task expected = new Task("name", null, null);
        expected.setId(TASK_ID);

        controller.saveTask(expected);

        verify(taskService).save(expected);
    }

    @Test
    public void shouldNotSaveNewTask() {
        Task expected = new Task("name", null, null);

        controller.saveTask(expected);

        verify(taskService, never()).save(expected);
    }

    @Test
    public void shouldSaveTaskAndRegisterHandlerForNewTrigger() {
        String subject = "trigger1";
        TaskActionInformation action = new TaskActionInformation("send", "action1", "action", "0.15", "send", new HashMap<String, String>());
        TaskEventInformation trigger = new TaskEventInformation("trigger", "trigger1", "trigger", "0.16", subject);
        Task expected = new Task("name", trigger, asList(action));

        when(eventListenerRegistryService.getListeners(subject)).thenReturn(new HashSet<EventListener>());


        controller.save(expected);

        verify(taskService).save(expected);
        verify(eventListenerRegistryService).getListeners(subject);
        verify(eventListenerRegistryService).registerListener(any(EventListener.class), eq(subject));
    }

    @Test
    public void shouldExportTask() throws Exception {
        String taskId = "12345";
        StringWriter writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        IOUtils.copy(this.getClass().getResourceAsStream("/new-task-version.json"), writer);
        ObjectNode node = (ObjectNode) mapper.readTree(writer.toString());
        node.remove(asList("validationErrors", "type", "_id", "_rev"));

        when(response.getWriter()).thenReturn(printWriter);
        when(taskService.exportTask(taskId)).thenReturn(node.toString());

        controller.exportTask(taskId, response);

        ArgumentCaptor<String> headerCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> jsonCaptor = ArgumentCaptor.forClass(String.class);

        verify(response).setContentType(APPLICATION_JSON_VALUE);
        verify(response).setCharacterEncoding(UTF_8);
        verify(response).setHeader(eq("Content-Disposition"), headerCaptor.capture());

        verify(printWriter).write(jsonCaptor.capture());

        assertEquals(
                format("attachment; filename=%s.json", encode("Pregnancy SMS", UTF_8)),
                headerCaptor.getValue()
        );

        assertEquals(node, mapper.readTree(jsonCaptor.getValue()));
    }

    @Test
    public void shouldImportTask() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        StringWriter writer = new StringWriter();
        IOUtils.copy(this.getClass().getResourceAsStream("/new-task-version.json"), writer);
        ObjectNode node = (ObjectNode) mapper.readTree(writer.toString());
        node.remove(asList("validationErrors", "type", "_rev"));
        String json = node.toString();

        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes()));

        controller.importTask(file);

        verify(taskService).importTask(json);
    }
}
