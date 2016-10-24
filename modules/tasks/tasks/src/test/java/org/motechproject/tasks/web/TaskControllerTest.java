package org.motechproject.tasks.web;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.dto.TaskActionInformationDto;
import org.motechproject.tasks.dto.TaskDto;
import org.motechproject.tasks.dto.TaskTriggerInformationDto;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TaskWebService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class TaskControllerTest {
    private static final Long TASK_ID = 12345l;

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

    @InjectMocks
    TaskController controller = new TaskController();

    @Mock
    TaskTriggerInformation trigger;

    @Mock
    Task task;

    @Mock
    TaskWebService taskWebService;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetAllTasks() {
        TaskActionInformationDto action = new TaskActionInformationDto("receive", "test", "receiveDisplay", "action1", "action",
                "0.15", "receive", "serviceInterface", "serviceMethod", new HashMap<>());
        TaskTriggerInformationDto trigger = new TaskTriggerInformationDto("send", "trigger1", "trigger", "0.16", "send",
                "send", "triggerSubject");

        List<TaskDto> expected = new ArrayList<>();
        expected.add(new TaskDto("name", trigger, asList(action)));
        expected.add(new TaskDto("name", trigger, asList(action)));
        expected.add(new TaskDto("name", trigger, asList(action)));

        when(taskWebService.getAllTasks()).thenReturn(expected);

        List<TaskDto> actual = controller.getAllTasks();

        verify(taskWebService, times(1)).getAllTasks();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTaskWithId() {
        TaskDto expected = new TaskDto();
        expected.setId(TASK_ID);

        when(taskWebService.getTask(expected.getId())).thenReturn(expected);

        TaskDto actual = controller.getTask(expected.getId());

        verify(taskWebService).getTask(expected.getId());

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
        expected.setTrigger(new TaskTriggerInformation());


        controller.saveTask(expected);

        verify(taskWebService).save(expected);
    }

    @Test
    public void shouldNotSaveNewTask() {
        Task expected = new Task("name", null, null);

        controller.saveTask(expected);

        verify(taskWebService, never()).save(expected);
    }

    @Test
    public void shouldSaveTask() {
        String subject = "trigger1";
        TaskActionInformation action = new TaskActionInformation("send", "action1", "action", "0.15", "send", new HashMap<String, String>());
        TaskTriggerInformation trigger = new TaskTriggerInformation("trigger", "trigger1", "trigger", "0.16", subject, subject);
        Task expected = new Task("name", trigger, asList(action));

        when(eventListenerRegistryService.getListeners(subject)).thenReturn(new HashSet<EventListener>());


        controller.save(expected);

        verify(taskWebService).save(expected);
    }

    @Test
    public void shouldExportTask() throws Exception {
        long taskId = 12345;
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

        when(taskService.importTask(json.toString())).thenReturn(new Task(null, trigger, null));
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes()));

        controller.importTask(file);

        verify(taskService).importTask(json);
    }
}
