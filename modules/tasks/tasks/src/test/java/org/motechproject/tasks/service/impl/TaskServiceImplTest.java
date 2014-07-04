package org.motechproject.tasks.service.impl;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.DataSource;
import org.motechproject.tasks.domain.EventParameter;
import org.motechproject.tasks.domain.FieldParameter;
import org.motechproject.tasks.domain.Filter;
import org.motechproject.tasks.domain.FilterSet;
import org.motechproject.tasks.domain.Lookup;
import org.motechproject.tasks.domain.LookupFieldsParameter;
import org.motechproject.tasks.domain.OperatorType;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskBuilder;
import org.motechproject.tasks.domain.TaskConfig;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskDataProviderObject;
import org.motechproject.tasks.domain.TaskError;
import org.motechproject.tasks.domain.TaskEvent;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.domain.TaskTriggerInformation;
import org.motechproject.tasks.domain.TriggerEvent;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskNotFoundException;
import org.motechproject.tasks.ex.TriggerNotFoundException;
import org.motechproject.tasks.ex.ValidationException;
import org.motechproject.tasks.repository.TasksDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.transaction.support.TransactionCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.ParameterType.UNICODE;
import static org.motechproject.tasks.events.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.events.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.events.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;
import static org.motechproject.tasks.events.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;

public class TaskServiceImplTest {

    private static final TaskTriggerInformation trigger = new TaskTriggerInformation("send", "test", "test-trigger", "0.15", "SEND");
    private static final TaskActionInformation action = new TaskActionInformation("receive", "test", "test-action", "0.14", "RECEIVE");

    @Mock
    TasksDataService tasksDataService;

    @Mock
    ChannelService channelService;

    @Mock
    TaskDataProviderService providerService;

    @Mock
    EventRelay eventRelay;

    @Mock
    BundleContext bundleContext;

    @Mock
    Bundle bundleTrigger;

    @Mock
    Bundle bundleAction;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    TaskServiceImpl taskService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        taskService = new TaskServiceImpl(tasksDataService, channelService, providerService, eventRelay, bundleContext);

        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundleTrigger, bundleAction});
        when(bundleTrigger.getSymbolicName()).thenReturn("test-trigger");
        when(bundleAction.getSymbolicName()).thenReturn("test-action");
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

        TaskConfig config = new TaskConfig().add(new DataSource(1234L, 1L, "Test", "id", asList(new Lookup("id", "trigger.value")), true));

        action.setValues(map);

        Task task = new Task("name", trigger, asList(action), config, true, false);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SENDING", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("receive", "RECEIVE", "", null)));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id", asList("id"))), null)));
        provider.setId(1234L);

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById(1234L)).thenReturn(provider);

        taskService.save(task);
    }

    @Test
    public void shouldSaveTaskWithEmptyActionInputFields() {
        Task task = new Task("name", trigger, asList(action), null, false, false);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("receive", "RECEIVE", "", null)));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id", asList("id"))), null)));
        provider.setId(1234L);

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById(1234L)).thenReturn(provider);

        taskService.save(task);

        verifyCreateAndCaptureTask();
    }

    @Test
    public void shouldSaveTask() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        TaskConfig config = new TaskConfig().add(new DataSource(1234L, 1L, "Test", "id", asList(new Lookup("id", "trigger.value")), true));

        action.setValues(map);

        Task task = new Task("name", trigger, asList(action), config, true, false);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);

        ActionEvent actionEvent = new ActionEvent("receive", "RECEIVE", "", null);
        actionEvent.addParameter(new ActionParameter("Phone", "phone"), true);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(actionEvent));

        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id", asList("id"))), null)));
        provider.setId(1234L);

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById(1234L)).thenReturn(provider);

        taskService.save(task);

        verifyCreateAndCaptureTask();
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

        when(tasksDataService.retrieveAll()).thenReturn(expected);

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

        when(tasksDataService.retrieveAll()).thenReturn(asList(t));

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
        long taskId = 12345L;

        Task expected = new Task();
        expected.setId(taskId);

        when(tasksDataService.findById(taskId)).thenReturn(expected);

        Task actual = taskService.getTask(taskId);

        assertEquals(expected, actual);
    }

    @Test
    public void shouldDeleteTask() {
        Task expected = new Task();
        expected.setId(12345L);

        when(tasksDataService.findById(expected.getId())).thenReturn(expected);

        taskService.deleteTask(expected.getId());

        verify(tasksDataService).findById(expected.getId());
        verify(tasksDataService).delete(expected);
    }

    @Test(expected = TaskNotFoundException.class)
    public void shouldThrowExceptionDuringDeletionIfTaskNotFound() {
        long taskId = 12345L;

        when(tasksDataService.findById(taskId)).thenReturn(null);

        taskService.deleteTask(taskId);
    }

    @Test
    public void shouldConvertTaskToJSON() throws Exception {
        long taskId = 12345L;
        Task expected = new TaskBuilder()
                .withName("test")
                .withTrigger(trigger)
                .addAction(action)
                .addDataSource(new DataSource(1234L, 1L, "Test", "id", asList(new Lookup("id", "trigger.value")), true))
                .isEnabled(true)
                .build();

        ObjectMapper mapper = new ObjectMapper();

        when(tasksDataService.findById(taskId)).thenReturn(expected);

        String json = taskService.exportTask(taskId);
        JsonNode node = mapper.readTree(json);
        notContainsIgnoreFields(node);

        assertEquals(expected, mapper.readValue(node, Task.class));
    }

    private void notContainsIgnoreFields(JsonNode node) {
        if (null == node) {
            return;
        }

        if (node.isObject()) {
            ObjectNode obj = (ObjectNode) node;

            for (String field : TaskServiceImpl.IGNORED_FIELDS) {
                assertFalse(obj.has(field));
            }
        }

        if (node.isArray()) {
            ArrayNode array = (ArrayNode) node;
            for (JsonNode item : array) {
                notContainsIgnoreFields(item);
            }
        }
    }

    @Test(expected = TaskNotFoundException.class)
    public void shouldThrowExceptionDuringExportingIfTaskNotFound() {
        long taskId = 12345;

        when(tasksDataService.findById(taskId)).thenReturn(null);

        taskService.exportTask(taskId);
    }

    @Test
    public void shouldImportTaskAndUpdateDataSourceIDs() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        action.getValues().put("phone", "{{ad.1234.Test#1.id}}");

        Task given = new TaskBuilder()
                .withName("test")
                .withTrigger(trigger)
                .addAction(action)
                .addDataSource(new DataSource("providerName", 1234L, 1L, "Test", "id", asList(new Lookup("id", "trigger.value")), true))
                .addFilterSet(new FilterSet(asList(new Filter("displayName", "ad.1234.Test#1.id", UNICODE, true, OperatorType.EXIST.getValue(), ""))))
                .isEnabled(true)
                .build();

        TaskDataProvider provider = new TaskDataProvider();
        provider.setName("providerName");
        provider.setId(56789L);
        provider.setObjects(
                asList(new TaskDataProviderObject("display", "Test",
                        asList(new LookupFieldsParameter("id", asList("id"))),
                        asList(new FieldParameter("display", "id", UNICODE))
                ))
        );

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);

        ActionEvent actionEvent = new ActionEvent("receive", "RECEIVE", "", null);
        actionEvent.addParameter(new ActionParameter("Phone", "phone"), true);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(actionEvent));

        when(providerService.getProviders()).thenReturn(asList(provider));
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);
        when(providerService.getProviderById(56789L)).thenReturn(provider);

        String json = mapper.writeValueAsString(given);

        taskService.importTask(json);

        action.getValues().put("phone", "{{ad.56789.Test#1.id}}");

        Task expected = new TaskBuilder()
                .withName("test")
                .withTrigger(trigger)
                .addAction(action)
                .addDataSource(new DataSource("providerName", 56789L, 1L, "Test", "id", asList(new Lookup("id", "trigger.value")), true))
                .addFilterSet(new FilterSet(asList(new Filter("displayName", "ad.56789.Test#1.id", UNICODE, true, OperatorType.EXIST.getValue(), ""))))
                .isEnabled(true)
                .build();

        verify(providerService).getProviders();

        Task actual = verifyCreateAndCaptureTask();
        assertEquals(expected, actual);
    }

    @Test
    public void shouldValidateTasksOfDependentModulesAfterChannelUpdateForInvalidTriggers() {
        Task task = new Task("name", trigger, new ArrayList<>(asList(action)), new TaskConfig(), true, false);
        task.setId(6l);
        when(tasksDataService.executeQuery(any(QueryExecution.class))).thenReturn(asList(task));
        when(tasksDataService.findById(6l)).thenReturn(task);

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent(
                "send", "SENDING", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("schedule", "SCHEDULE", "", null)));
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(trigger));

        Task actualTask = verifyUpdateAndCaptureTask(times(2));

        assertFalse(actualTask.isEnabled());
        List<Object> errors = new ArrayList<Object>(actualTask.getValidationErrors());
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(hasProperty("message", equalTo("task.validation.error.triggerNotExist"))));
    }

    @Test
    public void shouldValidateTasksAfterChannelUpdateForInvalidActions() {
        Task task = new Task("name", trigger, new ArrayList<>(asList(action)), new TaskConfig(), true, false);
        task.setId(124l);
        when(tasksDataService.executeQuery(any(QueryExecution.class))).thenReturn(asList(task));
        when(tasksDataService.findById(124l)).thenReturn(task);

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "",
                asList(new TriggerEvent("send", "SENDING", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null,
                asList(new ActionEvent("schedule", "SCHEDULE", "", null)));
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(action));

        Task actualTask = verifyUpdateAndCaptureTask();

        assertFalse(actualTask.isEnabled());
        List<Object> errors = new ArrayList<Object>(actualTask.getValidationErrors());
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(hasProperty("message", equalTo("task.validation.error.actionNotExist"))));
    }

    @Test
    public void shouldValidateTasksAfterChannelUpdateForInvalidTaskDataProviders() {
        TaskConfig config = new TaskConfig().add(new DataSource(1234L, 1L, "Test", "id", asList(new Lookup("id", "trigger.value")), true));
        Task task = new Task("name", trigger, asList(action), config, true, false);
        task.setId(12345l);
        List<LookupFieldsParameter> lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldsParameter("property", asList("property")));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", lookupFields, null)));
        provider.setId(1234L);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("receive", "RECEIVE", "", null)));

        when(tasksDataService.findById(12345l)).thenReturn(task);
        when(tasksDataService.retrieveAll()).thenReturn(asList(task));
        when(providerService.getProvider(provider.getName())).thenReturn(provider);

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        taskService.validateTasksAfterTaskDataProviderUpdate(getProviderUpdateEvent(provider.getName()));

        Task actualTask = verifyUpdateAndCaptureTask();

        assertFalse(actualTask.isEnabled());
        List<Object> errors = new ArrayList<Object>(actualTask.getValidationErrors());
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(hasProperty("message", equalTo("task.validation.error.providerObjectLookupNotExist"))));
    }

    @Test
    public void shouldValidateTasksAfterChannelUpdateForValidTriggers() {
        Task task = new Task("name", trigger, asList(action), new TaskConfig(), true, false);
        Set<TaskError> existingErrors = new HashSet<>();
        existingErrors.add(new TaskError("task.validation.error.triggerNotExist"));
        task.addValidationErrors(existingErrors);
        when(tasksDataService.executeQuery(any(QueryExecution.class))).thenReturn(asList(task));

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("schedule", "SCHEDULE", "", null)));
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(trigger));

        Task actualTask = verifyCreateAndCaptureTask();

        assertTrue(actualTask.isEnabled());
        assertTrue(actualTask.getValidationErrors().isEmpty());
    }

    @Test
    public void shouldValidateTasksAfterChannelUpdateForValidTaskDataProviders() {
        TaskConfig config = new TaskConfig().add(new DataSource(1234L, 1L, "Test", "id", asList(new Lookup("id", "trigger.value")), true));
        Task task = new Task("name", trigger, asList(action), config, true, false);
        Set<TaskError> existingErrors = new HashSet<>();
        existingErrors.add(new TaskError("task.validation.error.providerObjectLookupNotExist"));
        task.addValidationErrors(existingErrors);

        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", null, null)));
        provider.setId(1234L);
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<String, Object>();
        hashMap.put("displayName", "id");
        ArrayList<String> list = new ArrayList<>();
        list.add("id");
        hashMap.put("fields", list);
        provider.getObjects().get(0).setLookupFields(asList((Object) hashMap));
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEvent("receive", "RECEIVE", "", null)));

        when(tasksDataService.retrieveAll()).thenReturn(asList(task));
        when(providerService.getProvider(provider.getName())).thenReturn(provider);
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        taskService.validateTasksAfterTaskDataProviderUpdate(getProviderUpdateEvent(provider.getName()));

        Task actualTask = verifyCreateAndCaptureTask();

        assertTrue(task.isEnabled());
        assertTrue(actualTask.getValidationErrors().isEmpty());
    }

    @Test
    public void shouldNotValidateTasksAfterChannelUpdateIfDataSourceDoesNotExistForGivenProvider() {
        TaskConfig config = new TaskConfig().add(new DataSource(1234L, 1L, "Test", "id", asList(new Lookup("id", "trigger.value")), true));
        Task task = new Task("name", trigger, asList(action), config, true, false);

        TaskDataProvider dataProvider = new TaskDataProvider("abc", null);
        dataProvider.setId(5678L);

        when(tasksDataService.retrieveAll()).thenReturn(asList(task));
        when(providerService.getProvider(dataProvider.getName())).thenReturn(dataProvider);

        taskService.validateTasksAfterTaskDataProviderUpdate(getProviderUpdateEvent("abc"));

        verify(tasksDataService, never()).create(any(Task.class));
        verify(tasksDataService, never()).update(any(Task.class));
    }

    @Test
    public void shouldRemoveValidationErrorAndUpdateTaskOnlyIfItHasAnyValidationErrors() {
        Task task = new Task("name", trigger, asList(action), new TaskConfig(), true, false);
        Set<TaskError> existingErrors = new HashSet<>();
        existingErrors.add(new TaskError("task.validation.error.triggerNotExist"));
        task.addValidationErrors(existingErrors);
        when(tasksDataService.executeQuery(any(QueryExecution.class))).thenReturn(asList(task));

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")))), null);
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(trigger));

        Task actualTask = verifyCreateAndCaptureTask();
        assertFalse(actualTask.hasValidationErrors());
    }

    @Test
    public void shouldNotUpdateTaskIfItDoesNotHaveAnyValidationErrors() {
        Task task = new Task("name", trigger, asList(action), new TaskConfig(), true, false);
        when(tasksDataService.executeQuery(any(QueryExecution.class))).thenReturn(asList(task));

        Channel channel = new Channel("test", "test-action", "0.14", "", null,
                asList(new ActionEvent(action.getDisplayName(), action.getSubject(), "", null)));
        when(channelService.getChannel(action.getModuleName())).thenReturn(channel);
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(channel);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(action));

        verify(tasksDataService, never()).create(any(Task.class));
        verify(tasksDataService, never()).update(any(Task.class));
        verify(tasksDataService, never()).doInTransaction(any(TransactionCallback.class));
    }

    @Test
    public void shouldFailValidationIfTriggerChannelIsNotRegistered() {
        TaskTriggerInformation trigger = new TaskTriggerInformation("triggerDisplay", "triggerChannel", "triggerModule", "1.0", "subject");
        TaskActionInformation action = new TaskActionInformation("actionDisplay", "actionChannel", "actionModule", "1.0", "subject");
        Task fooTask = new TaskBuilder().withName("foo").withTrigger(trigger).withTaskConfig(new TaskConfig()).addAction(action).build();
        fooTask.setEnabled(true);
        when(channelService.getChannel("foo-module")).thenReturn(null);
        when(channelService.getChannel("actionModule")).thenReturn(null);

        expectedException.expect(ValidationException.class);
        expectedException.expect(new TypeSafeMatcher<ValidationException>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            public boolean matchesSafely(ValidationException actualException) {
                final TaskError triggerChannelError = new TaskError("task.validation.error.triggerChannelNotRegistered");
                final TaskError actionChannelError = new TaskError("task.validation.error.actionChannelNotRegistered");

                Set<TaskError> taskErrors = actualException.getTaskErrors();
                return taskErrors.contains(triggerChannelError) && taskErrors.contains(actionChannelError);
            }
        });

        taskService.save(fooTask);
    }

    private Task verifyUpdateAndCaptureTask() {
        return captureTask(true, null);
    }

    private Task verifyUpdateAndCaptureTask(VerificationMode verificationMode) {
        return captureTask(true, verificationMode);
    }

    private Task verifyCreateAndCaptureTask() {
        return captureTask(false, null);
    }

    private Task captureTask(boolean fromUpdate, VerificationMode verificationMode) {
        VerificationMode mode = (verificationMode == null) ? times(1) : verificationMode;

        ArgumentCaptor<TransactionCallback> transactionCaptor = ArgumentCaptor.forClass(TransactionCallback.class);
        verify(tasksDataService, mode).doInTransaction(transactionCaptor.capture());
        transactionCaptor.getValue().doInTransaction(null);

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        if (fromUpdate) {
            verify(tasksDataService).update(taskCaptor.capture());
        } else {
            verify(tasksDataService).create(taskCaptor.capture());
        }

        return taskCaptor.getValue();
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
