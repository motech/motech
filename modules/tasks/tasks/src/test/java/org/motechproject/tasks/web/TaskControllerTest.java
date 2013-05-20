package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TaskTriggerHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskControllerTest {
    private static final String TASK_ID = "12345";

    @Mock
    TaskService taskService;

    @Mock
    TaskActivityService messageService;

    @Mock
    EventListenerRegistryService eventListenerRegistryService;

    TaskTriggerHandler taskTriggerHandler;

    TaskController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        taskTriggerHandler = new TaskTriggerHandler(null, null, eventListenerRegistryService, null, null);
        controller = new TaskController(taskService, messageService, taskTriggerHandler);
    }

    @Test
    public void shouldGetAllTasks() {
        TaskActionInformation action = new TaskActionInformation("receive", "action1", "action", "0.15", "receive");
        TaskEventInformation trigger = new TaskEventInformation("send", "trigger1", "trigger", "0.16", "send");

        List<Task> expected = new ArrayList<>();
        expected.add(new Task("name", trigger, action, new HashMap<String, String>()));
        expected.add(new Task("name", trigger, action, new HashMap<String, String>()));
        expected.add(new Task("name", trigger, action, new HashMap<String, String>()));

        when(taskService.getAllTasks()).thenReturn(expected);

        List<Task> actual = controller.getAllTasks();

        verify(taskService).getAllTasks();

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
        Task expected = new Task("name", null, null, new HashMap<String, String>());
        expected.setId(TASK_ID);

        controller.saveTask(expected);

        verify(taskService).save(expected);
    }

    @Test
    public void shouldNotSaveNewTask() {
        Task expected = new Task("name", null, null, new HashMap<String, String>());

        controller.saveTask(expected);

        verify(taskService, never()).save(expected);
    }

    @Test
    public void shouldSaveTaskAndRegisterHandlerForNewTrigger() {
        String subject = "trigger1";
        TaskActionInformation action = new TaskActionInformation("send", "action1", "action", "0.15", "send");
        TaskEventInformation trigger = new TaskEventInformation("trigger", "trigger1", "trigger", "0.16", subject);
        Task expected = new Task("name", trigger, action, new HashMap<String, String>());

        when(eventListenerRegistryService.getListeners(subject)).thenReturn(new HashSet<EventListener>());


        controller.save(expected);

        verify(taskService).save(expected);
        verify(eventListenerRegistryService).getListeners(subject);
        verify(eventListenerRegistryService).registerListener(any(EventListener.class), eq(subject));
    }
}
