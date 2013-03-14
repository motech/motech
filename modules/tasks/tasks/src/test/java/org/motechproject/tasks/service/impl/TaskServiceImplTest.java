package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.AllTasks;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskServiceImplTest {
    private static final TaskEventInformation trigger = new TaskEventInformation("test", "test", "0.15", "SEND");
    private static final TaskActionInformation action = new TaskActionInformation("test", "test", "0.14", "RECEIVE");

    @Mock
    AllTasks allTasks;

    @Mock
    ChannelService channelService;

    TaskService taskService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        taskService = new TaskServiceImpl(allTasks, channelService);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutTrigger() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task(null, action, map, "name");

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutAction() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task(trigger, null, map, "name");

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutActionInputFields() {
        Task t = new Task(trigger, action, null, "name");

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutName() {
        Task t = new Task(trigger, action, null, null);

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithNameWithContainsOnlyWhitespaces() {
        Task t = new Task(trigger, action, null, "     ");

        taskService.save(t);
    }

    @Test
    public void shouldSaveTaskWithEmptyActionInputFields() {
        Task t = new Task(trigger, action, new HashMap<String, String>(), "name");

        taskService.save(t);

        verify(allTasks).addOrUpdate(t);
    }

    @Test
    public void shouldSaveTask() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task(trigger, action, map, "name");

        taskService.save(t);

        verify(allTasks).addOrUpdate(t);
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowActionNotFoundExceptionWhenChannelNotContainsActions() throws ActionNotFoundException {
        Channel c = new Channel();

        when(channelService.getChannel("test", "test", "0.14")).thenReturn(c);

        taskService.getActionEventFor(new Task(null, action, null, null));
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowActionNotFoundExceptionWhenChannelContainsEmptyActionList() throws ActionNotFoundException {
        Channel c = new Channel();
        c.setActionTaskEvents(new ArrayList<ActionEvent>());

        when(channelService.getChannel("test", "test", "0.14")).thenReturn(c);

        taskService.getActionEventFor(new Task(null, action, null, null));
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowActionNotFoundException() throws ActionNotFoundException {
        List<ActionEvent> actionEvents = new ArrayList<>();
        actionEvents.add(new ActionEvent());

        Channel c = new Channel();
        c.setActionTaskEvents(actionEvents);

        when(channelService.getChannel("test", "test", "0.14")).thenReturn(c);

        taskService.getActionEventFor(new Task(null, action, null, null));
    }

    @Test
    public void shouldFindActionForGivenTask() throws ActionNotFoundException {
        Task t = new Task(trigger, action, new HashMap<String, String>(), "name");

        ActionEvent expected = new ActionEvent();
        expected.setSubject(action.getSubject());
        expected.setDisplayName("Receive");

        Channel c = new Channel();
        c.setActionTaskEvents(asList(expected));

        when(channelService.getChannel("test", "test", "0.14")).thenReturn(c);

        TaskEvent actual = taskService.getActionEventFor(t);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetAllTasks() {
        ArrayList<Task> expected = new ArrayList<>();
        expected.add(new Task());
        expected.add(new Task());

        when(allTasks.getAll()).thenReturn(expected);

        List<Task> actual = taskService.getAllTasks();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotFindTasksForGivenTrigger() {
        Task t = new Task(action, null, new HashMap<String, String>(), "name");

        TriggerEvent triggerEvent = new TriggerEvent();
        triggerEvent.setSubject(trigger.getSubject());

        when(allTasks.getAll()).thenReturn(asList(t));

        List<Task> tasks = taskService.findTasksForTrigger(triggerEvent);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void shouldFindTasksForGivenTrigger() {
        Task t = new Task(trigger, action, new HashMap<String, String>(), "name");

        TriggerEvent triggerEvent = new TriggerEvent();
        triggerEvent.setSubject("SEND");

        when(allTasks.getAll()).thenReturn(asList(t));

        List<Task> tasks = taskService.findTasksForTrigger(triggerEvent);

        assertEquals(asList(t), tasks);
    }

    @Test(expected = TriggerNotFoundException.class)
    public void shouldThrowTriggerNotFoundExceptionWhenChannelListIsEmpty() throws TriggerNotFoundException {
        when(channelService.getAllChannels()).thenReturn(new ArrayList<Channel>());

        taskService.findTrigger(trigger.getSubject());
    }

    @Test(expected = TriggerNotFoundException.class)
    public void shouldThrowTriggerNotFoundExceptionWhenChannelContainsEmptyTriggerList() throws TriggerNotFoundException {
        Channel c = new Channel();
        c.setTriggerTaskEvents(new ArrayList<TriggerEvent>());

        when(channelService.getAllChannels()).thenReturn(asList(c));

        taskService.findTrigger(trigger.getSubject());
    }

    @Test(expected = TriggerNotFoundException.class)
    public void shouldThrowTriggerNotFoundException() throws TriggerNotFoundException {
        TriggerEvent triggerEvent = new TriggerEvent();
        triggerEvent.setSubject(action.getSubject());

        Channel c = new Channel();
        c.setTriggerTaskEvents(asList(triggerEvent));

        when(channelService.getAllChannels()).thenReturn(asList(c));

        taskService.findTrigger(trigger.getSubject());
    }

    @Test
    public void shouldFindTriggerForGivenSubject() throws TriggerNotFoundException {
        TriggerEvent triggerEvent = new TriggerEvent();
        triggerEvent.setSubject("RECEIVE");

        Channel c = new Channel();
        c.setTriggerTaskEvents(asList(triggerEvent));

        when(channelService.getAllChannels()).thenReturn(asList(c));

        TaskEvent actual = taskService.findTrigger("RECEIVE");

        assertEquals(triggerEvent, actual);
    }

    @Test
    public void shouldGetTaskById() {
        String taskId = "12345";

        Task expected = new Task();
        expected.setId(taskId);

        when(allTasks.get(taskId)).thenReturn(expected);

        Task actual = taskService.getTask(taskId);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldDeleteTask() {
        Task expected = new Task();
        expected.setId("12345");

        when(allTasks.get(expected.getId())).thenReturn(expected);

        taskService.deleteTask(expected.getId());

        verify(allTasks).get(expected.getId());
        verify(allTasks).remove(expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenTaskNotFound() {
        String taskId = "12345";

        when(allTasks.get(taskId)).thenReturn(null);

        taskService.deleteTask(taskId);
    }

}
