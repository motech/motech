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
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.LookupFieldsParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskConfig;
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

import java.util.*;

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
        Task t = new Task("name", null, asList(action));

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutAction() {
        Task t = new Task("name", trigger, null);

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithoutName() {
        Task t = new Task(null, trigger, asList(action));

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithNameWithContainsOnlyWhitespaces() {
        Task t = new Task("     ", trigger, asList(action));

        taskService.save(t);
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskIfNotMatchExtraValidationConditions() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        TaskConfig config = new TaskConfig().add(new DataSource("1234", 1L, "Test", "id", asList(new DataSource.Lookup("id", "trigger.value")), true));

        action.setValues(map);

        Task task = new Task("name", trigger, asList(action), config, true);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SENDING", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("receive", "RECEIVE", "", null)));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id",asList("id"))), null)));
        provider.setId("1234");

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById("1234")).thenReturn(provider);

        taskService.save(task);
    }

    @Test
    public void shouldSaveTaskWithEmptyActionInputFields() {
        Task task = new Task("name", trigger, asList(action), null, false);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("receive", "RECEIVE", "", null)));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id",asList("id"))), null)));
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

        TaskConfig config = new TaskConfig().add(new DataSource("1234", 1L, "Test", "id", asList(new DataSource.Lookup("id", "trigger.value")), true));

        action.setValues(map);

        Task task = new Task("name", trigger, asList(action), config, true);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);

        ActionEvent actionEvent = new ActionEvent("receive", "RECEIVE", "", null);
        actionEvent.addParameter(new ActionParameter("Phone", "phone"), true);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(actionEvent));

        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id",asList("id"))), null)));
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

        taskService.getActionEventFor(action);
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowActionNotFoundExceptionWhenChannelContainsEmptyActionList() throws ActionNotFoundException {
        Channel c = new Channel();
        c.setActionTaskEvents(new ArrayList<ActionEvent>());

        when(channelService.getChannel("test-action")).thenReturn(c);

        taskService.getActionEventFor(action);
    }

    @Test(expected = ActionNotFoundException.class)
    public void shouldThrowActionNotFoundException() throws ActionNotFoundException {
        List<ActionEvent> actionEvents = new ArrayList<>();
        actionEvents.add(new ActionEvent());

        Channel c = new Channel();
        c.setActionTaskEvents(actionEvents);

        when(channelService.getChannel("test-action")).thenReturn(c);

        taskService.getActionEventFor(action);
    }

    @Test
    public void shouldFindActionForGivenTask() throws ActionNotFoundException {
        Task task = new Task("name", trigger, asList(action));

        ActionEvent expected = new ActionEvent();
        expected.setSubject(action.getSubject());
        expected.setDisplayName("Receive");

        Channel c = new Channel();
        c.setActionTaskEvents(asList(expected));

        when(channelService.getChannel("test-action")).thenReturn(c);

        TaskEvent actual = taskService.getActionEventFor(task);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldFindActionForGivenInformation() throws ActionNotFoundException {
        ActionEvent expected = new ActionEvent();
        expected.setSubject(action.getSubject());
        expected.setDisplayName("Receive");

        Channel c = new Channel();
        c.setActionTaskEvents(asList(expected));

        when(channelService.getChannel("test-action")).thenReturn(c);

        TaskEvent actual = taskService.getActionEventFor(action);

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
        TriggerEvent triggerEvent = new TriggerEvent();
        triggerEvent.setSubject(trigger.getSubject());

        List<Task> tasks = taskService.findTasksForTrigger(triggerEvent);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());

        triggerEvent.setSubject(null);
        tasks = taskService.findTasksForTrigger(triggerEvent);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());

        tasks = taskService.findTasksForTrigger(null);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void shouldFindTasksForGivenTrigger() {
        Task t = new Task("name", trigger, asList(action));

        TriggerEvent triggerEvent = new TriggerEvent();
        triggerEvent.setSubject(trigger.getSubject());

        when(allTasks.byTriggerSubject("SEND")).thenReturn(asList(t));

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
        List<LookupFieldsParameter> lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldsParameter("property", asList("property")));

        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        TaskConfig config = new TaskConfig().add(new DataSource("1234", 1L, "Test", "id", asList(new DataSource.Lookup("id", "trigger.value")), true));

        action.setValues(map);

        Task task = new Task("name", trigger, asList(action), config, true);
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

        LinkedHashMap hashMap = new LinkedHashMap<String, List<Object>>();
        hashMap.put("displayName", "id");
        ArrayList list = new ArrayList<String>();
        list.add("id");
        hashMap.put("fields", list);

        provider.getObjects().get(0).setLookupFields(asList((Object) hashMap));
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