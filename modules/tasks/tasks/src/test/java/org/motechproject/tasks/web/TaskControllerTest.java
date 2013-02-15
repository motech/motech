package org.motechproject.tasks.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TaskTriggerHandler;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest(TaskTriggerHandler.class)
public class TaskControllerTest {
    private static final String TASK_ID = "12345";

    @Mock
    TaskService taskService;

    @Mock
    TaskActivityService messageService;

    @Mock
    TaskTriggerHandler taskTriggerHandler;

    TaskController controller;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        controller = new TaskController(taskService, messageService, taskTriggerHandler);
    }

    @Test
    public void shouldGetAllTasks() {
        List<Task> expected = new ArrayList<>();
        expected.add(new Task("trigger1", "action1", new HashMap<String, String>(), "name"));
        expected.add(new Task("trigger2", "action2", new HashMap<String, String>(), "name"));
        expected.add(new Task("trigger3", "action3", new HashMap<String, String>(), "name"));

        when(taskService.getAllTasks()).thenReturn(expected);

        List<Task> actual = controller.getAllTasks();

        verify(taskService).getAllTasks();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetTaskWithId() {
        Task expected = new Task();
        Map<String,String> actionFields = new HashMap<String, String>();
        actionFields.put("1", "test");
        expected.setId(TASK_ID);
        expected.setActionInputFields(actionFields);

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
        Task expected = new Task("trigger1", "action1", new HashMap<String, String>(), "name");
        expected.setId(TASK_ID);

        controller.saveTask(expected);

        verify(taskService).save(expected);
    }

    @Test
    public void shouldNotSaveNewTask() {
        Task expected = new Task("trigger1", "action1", new HashMap<String, String>(), "name");

        controller.saveTask(expected);

        verify(taskService, never()).save(expected);
    }

    @Test
    public void shouldSaveTaskAndRegisterHandlerForNewTrigger() {
        String subject = "trigger1";
        Task expected = new Task(format("channel1:module1:0.15:%s", subject), "action1", new HashMap<String, String>(), "name");

        controller.save(expected);

        verify(taskService).save(expected);
        verify(taskTriggerHandler).registerHandlerFor(subject);
    }
}
