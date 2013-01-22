package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.motechproject.tasks.repository.AllTasks;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class TaskServiceImplTest {
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

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSaveTaskWithIllegalTrigger() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task("test:SEND", "test:test:0.14:RECEIVE", map, "name");

        taskService.save(t);

        verify(allTasks, never()).addOrUpdate(t);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSaveTaskWithIllegalAction() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task("test:test:0.15:SEND", "test:test:RECEIVE", map, "name");

        taskService.save(t);

        verify(allTasks, never()).addOrUpdate(t);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotSaveTaskWithNullActionInputFields() {
        Task t = new Task("test:test:0.15:SEND", "test:test:0.14:RECEIVE", null, "name");

        taskService.save(t);

        verify(allTasks, never()).addOrUpdate(t);
    }

    @Test
    public void shouldSaveTaskWithEmptyActionInputFields() {
        Task t = new Task("test:test:0.15:SEND", "test:test:0.14:RECEIVE", new HashMap<String, String>(), "name");

        taskService.save(t);

        verify(allTasks).addOrUpdate(t);
    }


    @Test
    public void shouldSaveTask() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task("test:test:0.15:SEND", "test:test:0.14:RECEIVE", map, "name");

        taskService.save(t);

        verify(allTasks).addOrUpdate(t);
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowExceptionWhenActionNotFound() throws ActionNotFoundException {
        String trigger = "Test 1:test-1:0.15:SEND";
        String action = "Test 3:test-3:0.15:RECEIVE";
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task(trigger, action, map, "name");
        Channel c = new Channel();

        when(channelService.getChannel("Test 3", "test-3", "0.15")).thenReturn(c);

        taskService.getActionEventFor(t);
    }

    @Test
    public void shouldFindActionForGivenTask() throws ActionNotFoundException {
        String trigger = "Test 1:test-1:0.15:SEND";
        String action = "Test 3:test-3:0.15:RECEIVE";
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task(trigger, action, map, "name");

        TaskEvent expected = new TaskEvent();
        expected.setSubject("RECEIVE");
        expected.setDisplayName("Receive");
        expected.setEventParameters(Arrays.asList(new EventParameter("Phone", "phone")));

        Channel c = new Channel();
        c.setActionTaskEvents(Arrays.asList(expected));

        when(channelService.getChannel("Test 3", "test-3", "0.15")).thenReturn(c);

        TaskEvent actual = taskService.getActionEventFor(t);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldFindTasksForGivenTrigger() {
        String trigger = "Test 1:test-1:0.15:SEND";
        String action = "Test 3:test-3:0.15:RECEIVE";
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task(trigger, action, map, "name");

        TaskEvent triggerEvent = new TaskEvent();
        triggerEvent.setSubject("SEND");

        Channel c = new Channel();
        c.setActionTaskEvents(Arrays.asList(triggerEvent));

        when(allTasks.getAll()).thenReturn(Arrays.asList(t));

        List<Task> tasks = taskService.findTasksForTrigger(triggerEvent);

        assertEquals(1, tasks.size());
        assertEquals(t, tasks.get(0));
    }

    @Test(expected = TriggerNotFoundException.class)
    public void shouldThrowExceptionWhenTriggerNotFound() throws TriggerNotFoundException {
        TaskEvent triggerEvent = new TaskEvent();
        triggerEvent.setSubject("RECEIVE");

        Channel c = new Channel();
        c.setTriggerTaskEvents(Arrays.asList(triggerEvent));

        when(channelService.getAllChannels()).thenReturn(Arrays.asList(c));

        taskService.findTrigger("SEND");
    }

    @Test
    public void shouldFindTriggerForGivenSubject() throws TriggerNotFoundException {
        TaskEvent triggerEvent = new TaskEvent();
        triggerEvent.setSubject("RECEIVE");

        Channel c = new Channel();
        c.setTriggerTaskEvents(Arrays.asList(triggerEvent));

        when(channelService.getAllChannels()).thenReturn(Arrays.asList(c));

        TaskEvent actual = taskService.findTrigger("RECEIVE");

        assertEquals(triggerEvent, actual);
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
