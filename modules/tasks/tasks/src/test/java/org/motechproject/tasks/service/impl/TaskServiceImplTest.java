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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.verification.VerificationMode;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mds.query.QueryExecution;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.builder.ActionEventBuilder;
import org.motechproject.tasks.domain.mds.channel.builder.ActionParameterBuilder;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.compatibility.TaskMigrationManager;
import org.motechproject.tasks.domain.mds.task.DataSource;
import org.motechproject.tasks.domain.mds.channel.EventParameter;
import org.motechproject.tasks.domain.mds.task.FieldParameter;
import org.motechproject.tasks.domain.mds.task.Filter;
import org.motechproject.tasks.domain.mds.task.FilterSet;
import org.motechproject.tasks.domain.mds.task.Lookup;
import org.motechproject.tasks.domain.mds.task.LookupFieldsParameter;
import org.motechproject.tasks.domain.mds.task.OperatorType;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.builder.TaskBuilder;
import org.motechproject.tasks.domain.mds.task.TaskConfig;
import org.motechproject.tasks.domain.mds.task.TaskDataProvider;
import org.motechproject.tasks.domain.mds.task.TaskDataProviderObject;
import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.channel.TaskEvent;
import org.motechproject.tasks.domain.mds.task.TaskEventInformation;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;
import org.motechproject.tasks.dto.TaskErrorDto;
import org.motechproject.tasks.exception.ActionNotFoundException;
import org.motechproject.tasks.exception.TaskNameAlreadyExistsException;
import org.motechproject.tasks.exception.TaskNotFoundException;
import org.motechproject.tasks.exception.ValidationException;
import org.motechproject.tasks.repository.TasksDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TriggerEventService;
import org.motechproject.tasks.service.TriggerHandler;
import org.motechproject.tasks.validation.TaskValidator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.enums.ParameterType.UNICODE;
import static org.motechproject.tasks.constants.EventDataKeys.CHANNEL_MODULE_NAME;
import static org.motechproject.tasks.constants.EventDataKeys.DATA_PROVIDER_NAME;
import static org.motechproject.tasks.constants.EventSubjects.CHANNEL_UPDATE_SUBJECT;
import static org.motechproject.tasks.constants.EventSubjects.DATA_PROVIDER_UPDATE_SUBJECT;

public class TaskServiceImplTest {

    private static final TaskTriggerInformation trigger = new TaskTriggerInformation("send", "test", "test-trigger", "0.15", "SEND", "SEND");
    private static final TaskActionInformation action = new TaskActionInformation("receive", "test", "test-action", "0.14", "RECEIVE");

    @Mock
    TasksDataService tasksDataService;

    @Mock
    ChannelService channelService;

    @Mock
    TriggerEventService triggerEventService;

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

    @Mock
    ServiceReference serviceReference;

    @Mock
    TriggerHandler triggerHandler;

    @Mock
    TaskMigrationManager taskMigrationManager;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    TaskServiceImpl taskService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        taskService = new TaskServiceImpl();
        taskService.setChannelService(channelService);
        taskService.setBundleContext(bundleContext);
        taskService.setEventRelay(eventRelay);
        taskService.setProviderService(providerService);
        taskService.setTasksDataService(tasksDataService);
        taskService.setTriggerEventService(triggerEventService);
        TaskValidator taskValidator = new TaskValidator();
        taskValidator.setTriggerEventService(triggerEventService);
        taskService.setTaskValidator(taskValidator);
        taskService.setTaskMigrationManager(taskMigrationManager);

        when(bundleContext.getBundles()).thenReturn(new Bundle[]{bundleTrigger, bundleAction});
        when(bundleTrigger.getSymbolicName()).thenReturn("test-trigger");
        when(bundleAction.getSymbolicName()).thenReturn("test-action");

        when(bundleContext.getServiceReference(eq(TriggerHandler.class))).thenReturn(serviceReference);
        when(bundleContext.getService(eq(serviceReference))).thenReturn(triggerHandler);
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
    public void shouldNotSaveTaskWithInvalidRetryNumberValue() {
        Task t = new Task("name", trigger, asList(action));
        t.setNumberOfRetries(-3);

        try {
            taskService.save(t);
        } finally {
            verifyZeroInteractions(tasksDataService);
        }
    }

    @Test(expected = ValidationException.class)
    public void shouldNotSaveTaskWithInvalidRetryIntervalValue() {
        Task t = new Task("name", trigger, asList(action));
        t.setRetryIntervalInMilliseconds(-10);

        try {
            taskService.save(t);
        } finally {
            verifyZeroInteractions(tasksDataService);
        }
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

        TaskConfig config = new TaskConfig().add(new DataSource("TestProvider", 1234L, 1L, "Test", "id", "specifiedName", asList(new Lookup("id", "trigger.value")), true));

        action.setValues(map);

        Task task = new Task("name", trigger, asList(action), config, true, false);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SENDING", "", asList(new EventParameter("test", "value")), "")), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEventBuilder()
                .setDisplayName("receive").setSubject("RECEIVE").setDescription("")
                .setActionParameters(null).build()));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id", asList("id"))), null)));
        provider.setId(1234L);
        Set<TaskError> errors = new HashSet<>();
        errors.add(new TaskError("task.validation.error.triggerNotExist", trigger.getDisplayName()));

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById(1234L)).thenReturn(provider);

        when(triggerEventService.validateTrigger(trigger)).thenReturn(errors);

        taskService.save(task);
    }

    @Test(expected = TaskNameAlreadyExistsException.class)
    public void shouldNotSaveTaskWithDuplicateName() {
        when(tasksDataService.findTasksByName("name")).thenReturn(
                asList(new Task("name", trigger, asList(action))));

        Task t = new Task("name", trigger, asList(action), null, false, false);

        try {
            taskService.save(t);
        } finally {
            verify(tasksDataService, times(1)).findTasksByName(t.getName());
            verifyNoMoreInteractions(tasksDataService);
        }
    }

    @Test
    public void shouldSaveTaskWithEmptyActionInputFields() {
        Task task = new Task("name", trigger, asList(action), null, false, false);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")), "")), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEventBuilder()
                .setDisplayName("receive").setSubject("RECEIVE").setDescription("")
                .setActionParameters(null).build()));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id", asList("id"))), null)));
        provider.setId(1234L);

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProviderById(1234L)).thenReturn(provider);

        taskService.save(task);
        verify(triggerHandler).registerHandlerFor(task.getTrigger().getEffectiveListenerSubject());
        // When task has not set number of retries, it should not register handler for retries
        verifyNoMoreInteractions(triggerHandler);

        verifyCreateAndCaptureTask();
    }

    @Test
    public void shouldSaveTask() {
        Map<String, String> map = new HashMap<>();
        map.put("phone", "12345");

        TaskConfig config = new TaskConfig().add(new DataSource("TestProvider", 1234L, 1L, "Test", "id", "specifiedName", asList(new Lookup("id", "trigger.value")), true));

        action.setValues(map);

        Task task = new Task("name", trigger, asList(action), config, true, false);
        task.setNumberOfRetries(5);
        task.setRetryTaskOnFailure(true);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")), "")), null);

        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("receive").setSubject("RECEIVE")
                .setDescription("").setActionParameters(null).build();
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Phone").setKey("phone").build(), true);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(actionEvent));

        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", asList(new LookupFieldsParameter("id", asList("id"))), null)));
        provider.setId(1234L);

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        when(providerService.getProvider("TestProvider")).thenReturn(provider);
        when(triggerEventService.triggerExists(task.getTrigger())).thenReturn(true);

        taskService.save(task);
        verify(triggerHandler).registerHandlerFor(task.getTrigger().getEffectiveListenerSubject());
        // Because task has set number of retries to 5, it should register retries handler for this task
        verify(triggerHandler).registerHandlerFor(task.getTrigger().getEffectiveListenerRetrySubject(), true);

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
        actionEvents.add(new ActionEventBuilder().build());

        Channel c = new Channel();
        c.setActionTaskEvents(actionEvents);

        when(channelService.getChannel("test-action")).thenReturn(c);

        taskService.getActionEventFor(action);
    }

    @Test
    public void shouldFindActionForGivenInformation() throws ActionNotFoundException {
        ActionEvent expected = new ActionEventBuilder().build();
        expected.setSubject(action.getSubject());
        expected.setDisplayName("receive");

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

        List<Task> tasks = taskService.findActiveTasksForTrigger(triggerEvent);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());

        triggerEvent.setSubject(null);
        tasks = taskService.findActiveTasksForTrigger(triggerEvent);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());

        tasks = taskService.findActiveTasksForTriggerSubject(null);

        assertNotNull(tasks);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void shouldFindTasksForGivenTrigger() {
        Task t = new Task("name", trigger, asList(action));

        TriggerEvent triggerEvent = new TriggerEvent();
        triggerEvent.setSubject(trigger.getSubject());

        when(tasksDataService.executeQuery(Matchers.<QueryExecution<Object>>any())).thenReturn(asList(t));

        List<Task> tasks = taskService.findActiveTasksForTrigger(triggerEvent);

        assertEquals(asList(t), tasks);
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
    public void shouldFindTasksByName() {
        String taskName = "test";

        List<Task> expected = new ArrayList<>();
        Task task = new Task();
        task.setName(taskName);
        expected.add(task);

        when(tasksDataService.findTasksByName(taskName)).thenReturn(expected);

        List<Task> actual = taskService.findTasksByName(taskName);

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
        Map<String, String> values = new HashMap<>();
        values.put("id", "1");
        action.setValues(values);

        long taskId = 12345L;
        Task expected = new TaskBuilder()
                .withName("test")
                .withTrigger(trigger)
                .addAction(action)
                .addDataSource(new DataSource("TestProvider", 1234L, 1L, "Test", "id", "specifiedName", asList(new Lookup("id", "trigger.value")), true))
                .isEnabled(true)
                .build();

        ObjectMapper mapper = new ObjectMapper();

        when(tasksDataService.findById(taskId)).thenReturn(expected);

        String json = taskService.exportTask(taskId);
        JsonNode node = mapper.readTree(json);
        notContainsIgnoreFields(node);

        // should preserve action values
        assertNotNull((node).findValue("id"));

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
    public void shouldImportTask() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        action.getValues().put("phone", "{{ad.providerName.Test#1.id}}");

        Task given = new TaskBuilder()
                .withName("test")
                .withTrigger(trigger)
                .addAction(action)
                .addDataSource(new DataSource("providerName", 1234L, 1L, "Test", "id", "specifiedName", asList(new Lookup("id", "trigger.value")), true))
                .addFilterSet(new FilterSet(asList(new Filter("displayName", "ad.providerName.Test#1.id", UNICODE, true, OperatorType.EXIST.getValue(), ""))))
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

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")), "")), null);

        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("receive").setSubject("RECEIVE")
                .setDescription("").setActionParameters(null).build();
        actionEvent.addParameter(new ActionParameterBuilder().setDisplayName("Phone").setKey("phone").build(), true);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(actionEvent));

        when(providerService.getProviders()).thenReturn(asList(provider));
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);
        when(triggerEventService.triggerExists(given.getTrigger())).thenReturn(true);
        when(providerService.getProvider("providerName")).thenReturn(provider);

        String json = mapper.writeValueAsString(given);

        taskService.importTask(json);

        action.getValues().put("phone", "{{ad.providerName.Test#1.id}}");

        Task expected = new TaskBuilder()
                .withName("test")
                .withTrigger(trigger)
                .addAction(action)
                .addDataSource(new DataSource("providerName", 56789L, 1L, "Test", "id", "specifiedName", asList(new Lookup("id", "trigger.value")), true))
                .addFilterSet(new FilterSet(asList(new Filter("displayName", "ad.providerName.Test#1.id", UNICODE, true, OperatorType.EXIST.getValue(), ""))))
                .isEnabled(true)
                .build();

        Task actual = verifyCreateAndCaptureTask();
        assertEquals(expected, actual);

        verify(taskMigrationManager).migrateTask(actual);
    }

    @Test
    public void shouldValidateTasksOfDependentModulesAfterChannelUpdateForInvalidTriggers() {
        Task task = new Task("name", trigger, new ArrayList<>(asList(action)), new TaskConfig(), true, false);
        task.setId(6l);
        when(tasksDataService.executeQuery(any(QueryExecution.class))).thenReturn(asList(task));
        when(tasksDataService.findById(6l)).thenReturn(task);

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent(
                "send", "SENDING", "", asList(new EventParameter("test", "value")), "")), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEventBuilder()
                .setDisplayName("schedule").setSubject("SCHEDULE").setDescription("")
                .setActionParameters(null).build()));
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);
        Set<TaskError> triggerValidationErrors = new HashSet<>();
        triggerValidationErrors.add(new TaskError("task.validation.error.triggerNotExist", trigger.getDisplayName()));
        when(triggerEventService.validateTrigger(eq(trigger))).thenReturn(triggerValidationErrors);

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
                asList(new TriggerEvent("send", "SENDING", "", asList(new EventParameter("test", "value")), "")), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null,
                asList(new ActionEventBuilder().setDisplayName("schedule").setSubject("SCHEDULE")
                        .setDescription("").setActionParameters(null).build()));
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(action));

        Task actualTask = verifyUpdateAndCaptureTask();

        assertFalse(actualTask.isEnabled());
        List<Object> errors = new ArrayList<>(actualTask.getValidationErrors());
        assertEquals(1, errors.size());
        assertThat(errors, hasItem(hasProperty("message", equalTo("task.validation.error.actionNotExist"))));
    }

    @Test
    public void shouldValidateTasksAfterChannelUpdateForInvalidTaskDataProviders() {
        TaskConfig config = new TaskConfig().add(new DataSource("TestProvider", 1234L, 1L, "Test", "id", "specifiedName",asList(new Lookup("id", "trigger.value")), true));
        Task task = new Task("name", trigger, asList(action), config, true, false);
        task.setId(12345l);
        List<LookupFieldsParameter> lookupFields = new ArrayList<>();
        lookupFields.add(new LookupFieldsParameter("property", asList("property")));
        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", lookupFields, null)));
        provider.setId(1234L);
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")), "")), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEventBuilder()
                .setDisplayName("receive").setSubject("RECEIVE").setDescription("")
                .setActionParameters(null).build()));

        when(tasksDataService.findById(12345l)).thenReturn(task);
        when(tasksDataService.retrieveAll()).thenReturn(asList(task));
        when(providerService.getProvider(provider.getName())).thenReturn(provider);

        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);

        taskService.validateTasksAfterTaskDataProviderUpdate(getProviderUpdateEvent(provider.getName()));

        Task actualTask = verifyUpdateAndCaptureTask();

        assertFalse(actualTask.isEnabled());
        List<Object> errors = new ArrayList<>(actualTask.getValidationErrors());
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

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")), "")), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEventBuilder()
                .setDisplayName("schedule").setSubject("SCHEDULE").setDescription("")
                .setActionParameters(null).build()));
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(channelService.getChannel(action.getModuleName())).thenReturn(actionChannel);
        when(triggerEventService.triggerExists(task.getTrigger())).thenReturn(true);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(trigger));

        Task actualTask = verifyCreateAndCaptureTask();

        assertTrue(actualTask.isEnabled());
        assertTrue(actualTask.getValidationErrors().isEmpty());
    }

    @Test
    public void shouldValidateTasksAfterChannelUpdateForValidTaskDataProviders() {
        TaskConfig config = new TaskConfig().add(new DataSource("TestProvider", 1234L, 1L, "Test", "id", "specifiedName", asList(new Lookup("id", "trigger.value")), true));
        Task task = new Task("name", trigger, asList(action), config, true, false);
        Set<TaskError> existingErrors = new HashSet<>();
        existingErrors.add(new TaskError("task.validation.error.providerObjectLookupNotExist"));
        task.addValidationErrors(existingErrors);

        TaskDataProvider provider = new TaskDataProvider("TestProvider", asList(new TaskDataProviderObject("test", "Test", null, null)));
        provider.setId(1234L);
        LinkedHashMap<String, Object> hashMap = new LinkedHashMap<>();
        hashMap.put("displayName", "id");
        ArrayList<String> list = new ArrayList<>();
        list.add("id");
        hashMap.put("fields", list);
        provider.getObjects().get(0).setLookupFields(asList((Object) hashMap));
        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")), "")), null);
        Channel actionChannel = new Channel("test", "test-action", "0.14", "", null, asList(new ActionEventBuilder()
                .setDisplayName("receive").setSubject("RECEIVE").setDescription("")
                .setActionParameters(null).build()));

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
        TaskConfig config = new TaskConfig().add(new DataSource("TestProvider", 1234L, 1L, "Test", "id", "specifiedName", asList(new Lookup("id", "trigger.value")), true));
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

        Channel triggerChannel = new Channel("test", "test-trigger", "0.15", "", asList(new TriggerEvent("send", "SEND", "", asList(new EventParameter("test", "value")), "")), null);
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(triggerChannel);
        when(triggerEventService.triggerExists(task.getTrigger())).thenReturn(true);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(trigger));

        Task actualTask = verifyCreateAndCaptureTask();
        assertFalse(actualTask.hasValidationErrors());
    }

    @Test
    public void shouldNotUpdateTaskIfItDoesNotHaveAnyValidationErrors() {
        Task task = new Task("name", trigger, asList(action), new TaskConfig(), true, false);
        when(tasksDataService.executeQuery(any(QueryExecution.class))).thenReturn(asList(task));

        Channel channel = new Channel("test", "test-action", "0.14", "", null,
                asList(new ActionEventBuilder().setDisplayName(action.getDisplayName()).setSubject(action.getSubject())
                        .setDescription("").setActionParameters(null).build()));
        when(channelService.getChannel(action.getModuleName())).thenReturn(channel);
        when(channelService.getChannel(trigger.getModuleName())).thenReturn(channel);

        taskService.validateTasksAfterChannelUpdate(getChannelUpdateEvent(action));

        verify(tasksDataService, never()).create(any(Task.class));
        verify(tasksDataService, never()).update(any(Task.class));
        verify(tasksDataService, never()).doInTransaction(any(TransactionCallback.class));
    }

    @Test
    public void shouldFailValidationIfTriggerChannelIsNotRegistered() {
        TaskTriggerInformation trigger = new TaskTriggerInformation("triggerDisplay", "triggerChannel", "triggerModule", "1.0", "subject", "triggerListenerSubject");
        TaskActionInformation action = new TaskActionInformation("actionDisplay", "actionChannel", "actionModule", "1.0", "subject");
        Task fooTask = new TaskBuilder().withName("foo").withTrigger(trigger).withTaskConfig(new TaskConfig()).addAction(action).build();
        fooTask.setEnabled(true);
        when(channelService.getChannel("foo-module")).thenReturn(null);
        when(channelService.getChannel("actionModule")).thenReturn(null);
        Set<TaskError> errors = new HashSet<>();
        errors.add(new TaskError("task.validation.error.triggerChannelNotRegistered"));
        when(triggerEventService.validateTrigger(trigger)).thenReturn(errors);

        expectedException.expect(ValidationException.class);
        expectedException.expect(new TypeSafeMatcher<ValidationException>() {
            @Override
            public void describeTo(Description description) {
            }

            @Override
            public boolean matchesSafely(ValidationException actualException) {
                final TaskErrorDto triggerChannelError = new TaskErrorDto("task.validation.error.triggerChannelNotRegistered");
                final TaskErrorDto actionChannelError = new TaskErrorDto("task.validation.error.actionChannelNotRegistered");

                Set<TaskErrorDto> taskErrors = actualException.getTaskErrors();
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

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);

        if (fromUpdate) {
            verify(tasksDataService, mode).update(taskCaptor.capture());
        } else {
            verify(tasksDataService, mode).create(taskCaptor.capture());
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
