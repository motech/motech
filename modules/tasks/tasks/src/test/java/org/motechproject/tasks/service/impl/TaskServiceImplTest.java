package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskAdditionalData;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskDataProviderObject;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.AllTasks;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.events.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.events.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;
import static org.motechproject.tasks.events.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;

public class TaskServiceImplTest {
    private static final TaskEventInformation trigger = new TaskEventInformation("send", "test", "test-trigger", "0.15", "SEND");
    private static final TaskActionInformation action = new TaskActionInformation("receive", "test", "test-action", "0.14", "RECEIVE");

    @Mock
    AllTasks allTasks;

    @Mock
    ChannelService channelService;

    @Mock
    TaskDataProviderService providerService;

    @Mock
    EventRelay eventRelay;

    TaskService taskService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        taskService = new TaskServiceImpl(allTasks, channelService, providerService, eventRelay);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutTrigger() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task("name", null, action, map);

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutAction() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Task t = new Task("name", trigger, null, map);

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutName() {
        Task t = new Task(null, trigger, action, null);

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithNameWithContainsOnlyWhitespaces() {
        Task t = new Task("     ", trigger, action, null);

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskIfNotMatchExtraValidationConditions() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Map<String, List<TaskAdditionalData>> additionalData = new HashMap<>();
        additionalData.put("1234", asList(new TaskAdditionalData(1L, "Test", "id", "trigger.value", true)));

        Task task = new Task("name", trigger, action, map, null, additionalData, true);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SENDING", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("receive", "RECEIVE", "", null)));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList("id"), null)));
        provider.setId("1234");

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById("1234")).thenReturn(provider);

        taskService.save(task);
    }

    @Test
    public void shouldSaveTaskWithEmptyActionInputFields() {
        Task task = new Task("name", trigger, action, new HashMap<String, String>(), null, null, false);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("receive", "RECEIVE", "", null)));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList("id"), null)));
        provider.setId("1234");

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById("1234")).thenReturn(provider);

        taskService.save(task);

        verify(allTasks).addOrUpdate(task);
    }

    @Test
    public void shouldSaveTask() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Map<String, List<TaskAdditionalData>> additionalData = new HashMap<>();
        additionalData.put("1234", asList(new TaskAdditionalData(1L, "Test", "id", "trigger.value", true)));

        Task task = new Task("name", trigger, action, map, null, additionalData, true);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);

        ActionEvent actionEvent = new ActionEvent("receive", "RECEIVE", "", null);
        actionEvent.addParameter(new ActionParameter("Phone", "phone"), true);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(actionEvent));

        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList("id"), null)));
        provider.setId("1234");

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById("1234")).thenReturn(provider);

        taskService.save(task);

        verify(allTasks).addOrUpdate(task);
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowActionNotFoundExceptionWhenChannelNotContainsActions() throws ActionNotFoundException {
        Channel c = new Channel();

        when(channelService.getChannel("test-action")).thenReturn(c);

        taskService.getActionEventFor(new Task(null, null, action, null));
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowActionNotFoundExceptionWhenChannelContainsEmptyActionList() throws ActionNotFoundException {
        Channel c = new Channel();
        c.setActionTaskEvents(new ArrayList<ActionEvent>());

        when(channelService.getChannel("test-action")).thenReturn(c);

        taskService.getActionEventFor(new Task(null, null, action, null));
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowActionNotFoundException() throws ActionNotFoundException {
        List<ActionEvent> actionEvents = new ArrayList<>();
        actionEvents.add(new ActionEvent());

        Channel c = new Channel();
        c.setActionTaskEvents(actionEvents);

        when(channelService.getChannel("test-action")).thenReturn(c);

        taskService.getActionEventFor(new Task(null, null, action, null));
    }

    @Test
    public void shouldFindActionForGivenTask() throws ActionNotFoundException {
        Task t = new Task("name", trigger, action, new HashMap<String, String>());

        ActionEvent expected = new ActionEvent();
        expected.setSubject(action.getSubject());
        expected.setDisplayName("Receive");

        Channel c = new Channel();
        c.setActionTaskEvents(asList(expected));

        when(channelService.getChannel("test-action")).thenReturn(c);

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
        Task t = new Task("name", action, null, new HashMap<String, String>());

        TriggerEvent triggerEvent = new TriggerEvent();
        triggerEvent.setSubject(trigger.getSubject());

        when(allTasks.getAll()).thenReturn(asList(t));

        List<Task> tasks = taskService.findTasksForTrigger(triggerEvent);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void shouldFindTasksForGivenTrigger() {
        Task t = new Task("name", trigger, action, new HashMap<String, String>());

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

    @Test
    public void shouldValidateTasksAfterUpdateEvents() {
        List<String> lookupFields = new ArrayList<>();
        lookupFields.add("property");

        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        Map<String, List<TaskAdditionalData>> additionalData = new HashMap<>();
        additionalData.put("1234", asList(new TaskAdditionalData(1L, "Test", "id", "trigger.value", true)));

        Task task = new Task("name", trigger, action, map, null, additionalData, true);
        when(allTasks.getAll()).thenReturn(asList(task));

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SENDING", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("schedule", "SCHEDULE", "", null)));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", lookupFields, null)));
        provider.setId("1234");

        TaskDataProvider dataProvider = new TaskDataProvider("abc", null);
        dataProvider.setId("5678");

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);
        when(providerService.getProvider(provider.getName())).thenReturn(provider);
        when(providerService.getProvider(dataProvider.getName())).thenReturn(dataProvider);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        ((TaskServiceImpl) taskService).validateTasksAfterChannelUpdate(getChannelUpdateEvent(trigger));
        ((TaskServiceImpl) taskService).validateTasksAfterChannelUpdate(getChannelUpdateEvent(action));

        verify(allTasks, times(2)).addOrUpdate(captor.capture());

        Task captured = captor.getValue();
        assertFalse(captured.isEnabled());
        assertFalse(captured.getValidationErrors().isEmpty());

        ArrayList<Object> arrayList = new ArrayList<Object>(captured.getValidationErrors());
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.triggerNotExist"))));
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.actionNotExist"))));

        ((TaskServiceImpl) taskService).validateTasksAfterTaskDataProviderUpdate(getProviderUpdateEvent(provider.getName()));

        verify(allTasks, times(3)).addOrUpdate(captor.capture());

        captured = captor.getValue();
        assertFalse(captured.isEnabled());
        assertFalse(captured.getValidationErrors().isEmpty());

        arrayList = new ArrayList<Object>(captured.getValidationErrors());
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.providerObjectLookupNotExist"))));
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.triggerNotExist"))));
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.actionNotExist"))));

        provider.getObjects().get(0).setLookupFields(asList("id"));
        ((TaskServiceImpl) taskService).validateTasksAfterTaskDataProviderUpdate(getProviderUpdateEvent(provider.getName()));

        verify(allTasks, times(4)).addOrUpdate(captor.capture());

        captured = captor.getValue();
        assertFalse(captured.isEnabled());
        assertFalse(captured.getValidationErrors().isEmpty());

        arrayList = new ArrayList<Object>(captured.getValidationErrors());
        assertThat(arrayList, not(hasItem(hasProperty("message", equalTo("validation.error.providerObjectLookupNotExist")))));
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.triggerNotExist"))));
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.actionNotExist"))));

        triggerChannel.getTriggerTaskEvents().get(0).setSubject("SEND");
        ((TaskServiceImpl) taskService).validateTasksAfterChannelUpdate(getChannelUpdateEvent(trigger));

        verify(allTasks, times(5)).addOrUpdate(captor.capture());

        captured = captor.getValue();
        assertFalse(captured.isEnabled());
        assertFalse(captured.getValidationErrors().isEmpty());

        arrayList = new ArrayList<Object>(captured.getValidationErrors());
        assertThat(arrayList, not(hasItem(hasProperty("message", equalTo("validation.error.providerObjectLookupNotExist")))));
        assertThat(arrayList, not(hasItem(hasProperty("message", equalTo("validation.error.triggerNotExist")))));
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.actionNotExist"))));

        ((TaskServiceImpl) taskService).validateTasksAfterTaskDataProviderUpdate(getProviderUpdateEvent("abc"));
        verify(allTasks, times(5)).addOrUpdate(captor.capture());

        captured = captor.getValue();
        assertFalse(captured.isEnabled());
        assertFalse(captured.getValidationErrors().isEmpty());

        arrayList = new ArrayList<Object>(captured.getValidationErrors());
        assertThat(arrayList, hasItem(hasProperty("message", equalTo("validation.error.actionNotExist"))));
    }

    private MotechEvent getChannelUpdateEvent(TaskEventInformation info) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(CHANNEL_MODULE_NAME, info.getModuleName());

        return new MotechEvent(CHANNEL_UPDATE_SUBJECT, parameters);
    }

    private MotechEvent getProviderUpdateEvent(String providerName) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(DATA_PROVIDER_NAME, providerName);

        return new MotechEvent(DATA_PROVIDER_UPDATE_SUBJECT, parameters);
    }

}
