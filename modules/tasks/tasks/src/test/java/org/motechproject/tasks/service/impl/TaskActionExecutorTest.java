package org.motechproject.tasks.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.constants.EventDataKeys;
import org.motechproject.tasks.domain.ObjectTest;
import org.motechproject.tasks.domain.mds.channel.ActionEvent;
import org.motechproject.tasks.domain.mds.channel.ActionParameter;
import org.motechproject.tasks.domain.mds.channel.builder.ActionEventBuilder;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.builder.TaskBuilder;
import org.motechproject.tasks.exception.ActionNotFoundException;
import org.motechproject.tasks.exception.TaskHandlerException;
import org.motechproject.tasks.service.TaskActivityService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.util.TaskContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.tasks.domain.enums.ParameterType.MAP;
import static org.motechproject.tasks.domain.enums.ParameterType.TEXTAREA;

@RunWith(MockitoJUnitRunner.class)
public class TaskActionExecutorTest {

    private static final Long TASK_ACTIVITY_ID  = 11L;

    @Mock
    private TaskService taskService;
    @Mock
    private TaskActivityService activityService;
    @Mock
    private EventRelay eventRelay;
    @Mock
    private BundleContext bundleContext;
    @Mock
    private TasksPostExecutionHandler postExecutionHandler;
    @InjectMocks
    private TaskActionExecutor taskActionExecutor = new TaskActionExecutor();

    @Test
    public void shouldRaiseEventIfActionHasSubject() throws ActionNotFoundException, TaskHandlerException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "actionSubject");
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("Action").setSubject("actionSubject")
                .setDescription("").setActionParameters(new TreeSet<>()).build();
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();
        task.setId(11L);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put(EventDataKeys.TASK_ID, 11L);
        metadata.put(EventDataKeys.TASK_RETRY, null);
        metadata.put(EventDataKeys.TASK_ACTIVITY_ID, TASK_ACTIVITY_ID);

        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, 0, new TaskContext(task, new HashMap<>(), metadata, activityService), TASK_ACTIVITY_ID);

        MotechEvent raisedEvent = new MotechEvent("actionSubject", new HashMap<>(), TasksEventCallbackService.TASKS_EVENT_CALLBACK_NAME, metadata);
        verify(eventRelay).sendEventMessage(raisedEvent);
    }

    @Test
    public void shouldRaiseEventWhenActionHasSubjectAndService_IfServiceIsNotAvailable() throws TaskHandlerException, ActionNotFoundException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("Action").setSubject("actionSubject")
                .setDescription("").setServiceInterface("serviceInterface").setServiceMethod("serviceMethod").setActionParameters(new TreeSet<ActionParameter>()).build();
        actionEvent.setActionParameters(new TreeSet<>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(null);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, 0,  new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService), TASK_ACTIVITY_ID);

        verify(eventRelay).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldNotRaiseEventIfActionHasSubjectAndService_IfServiceIsAvailable() throws ActionNotFoundException, TaskHandlerException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("Action").setSubject("actionSubject")
                .setDescription("").setServiceInterface("serviceInterface").setServiceMethod("serviceMethod")
                .setActionParameters(new TreeSet<>()).build();
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        ServiceReference serviceReference = mock(ServiceReference.class);
        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(new TestService());

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, 0, new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService), TASK_ACTIVITY_ID);

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldInvokeServiceIfActionHasService() throws ActionNotFoundException, TaskHandlerException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("Action")
                .setDescription("").setServiceInterface("serviceInterface").setServiceMethod("serviceMethod")
                .setActionParameters(new TreeSet<>()).build();
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        ServiceReference serviceReference = mock(ServiceReference.class);
        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(serviceReference);
        TestService testService = new TestService();
        when(bundleContext.getService(serviceReference)).thenReturn(testService);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, 0, new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService), TASK_ACTIVITY_ID);

        assertTrue(testService.serviceMethodInvoked());
    }

    @Test(expected = TaskHandlerException.class)
    public void shouldThrowExceptionIfBundleContextIsNotAvailable() throws TaskHandlerException, ActionNotFoundException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("Action")
                .setDescription("").setServiceInterface("serviceInterface").setServiceMethod("serviceMethod")
                .setActionParameters(new TreeSet<>()).build();
        actionEvent.setActionParameters(new TreeSet<>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();


        taskActionExecutor.execute(task, actionInformation, 0, new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService), TASK_ACTIVITY_ID);
    }

    @Test(expected = TaskHandlerException.class)
    public void shouldThrowExceptionIfActionHasNeitherEventNorService() throws TaskHandlerException, ActionNotFoundException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("Action")
                .setDescription("").setServiceInterface("serviceInterface").setServiceMethod("serviceMethod")
                .setActionParameters(new TreeSet<>()).build();
        actionEvent.setActionParameters(new TreeSet<>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        taskActionExecutor.execute(task, actionInformation, 0, new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService), TASK_ACTIVITY_ID);
    }

    @Test
    public void shouldAddActivityNotificationIfServiceIsNotAvailable() throws TaskHandlerException, ActionNotFoundException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("Action").setSubject("actionSubject")
                .setDescription("").setServiceInterface("serviceInterface").setServiceMethod("serviceMethod")
                .setActionParameters(new TreeSet<>()).build();
        actionEvent.setActionParameters(new TreeSet<>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(null);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, 0, new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService), TASK_ACTIVITY_ID);

        verify(activityService).addWarning(task, "task.warning.serviceUnavailable", "serviceInterface");
    }

    @Test
    public void shouldExecuteTaskIfActionMapParameterHasValueWithMixedTypes() throws Exception {
        TaskActionInformation actionInformation = prepareTaskActionInformationWithTrigger();
        ActionEvent actionEvent = prepareActionEvent();

        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, 0, prepareTaskContext(task), TASK_ACTIVITY_ID);

        verify(eventRelay).sendEventMessage(eq(prepareMotechEvent()));
    }

    @Test
    public void shouldExecuteTaskAndUsePostActionParameters() throws Exception {
        TaskActionInformation actionInformation = prepareTaskActionInformationWithService("key", "value");
        TaskActionInformation actionInformationWithPostActionParameter = prepareTaskActionInformationWithService("key", "{{pa.0.testKey}}");
        ActionEvent actionEvent = prepareActionEventWithService();

        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);
        when(taskService.getActionEventFor(actionInformationWithPostActionParameter)).thenReturn(actionEvent);

        ServiceReference serviceReference = mock(ServiceReference.class);

        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(new TestService());

        Task task = new Task();
        task.addAction(actionInformation);
        task.addAction(actionInformationWithPostActionParameter);

        taskActionExecutor.setBundleContext(bundleContext);

        TaskContext taskContext = new TaskContext(task, new HashMap<>(), new HashMap<>(), activityService);

        for (TaskActionInformation action : task.getActions()) {
            taskActionExecutor.execute(task, action, task.getActions().indexOf(action), taskContext, TASK_ACTIVITY_ID);
        }

        assertEquals("testObject", taskContext.getPostActionParameterValue("0", "testKey"));
        assertEquals("testObject", taskContext.getPostActionParameterValue("1", "testKey"));

    }

    private MotechEvent prepareMotechEvent() {
        Map<String, Object> parameters = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value123");
        parameters.put("map", map);
        return new MotechEvent("actionSubject", parameters, TasksEventCallbackService.TASKS_EVENT_CALLBACK_NAME, new HashMap<>());
    }

    private TaskContext prepareTaskContext(Task task) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("id", 123);
        return new TaskContext(task, parameters, new HashMap<>(), activityService);
    }
    private TaskActionInformation prepareTaskActionInformation() {
        TaskActionInformation actionInformation = new TaskActionInformation();
        actionInformation.setDisplayName("action");
        actionInformation.setChannelName("channel");
        actionInformation.setModuleName("module");
        actionInformation.setModuleVersion("0.1");
        actionInformation.setSubject("actionSubject");

        return actionInformation;
    }

    private TaskActionInformation prepareTaskActionInformationWithTrigger() {
        TaskActionInformation actionInformation = prepareTaskActionInformation();

        Map<String, String> values = new HashMap<>();
        values.put("map", "key1:value{{trigger.id}}");
        actionInformation.setValues(values);

        return actionInformation;
    }

    private TaskActionInformation prepareTaskActionInformationWithService(String key, String value) {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");

        Map<String, String> values = new HashMap<>();
        values.put(key, value);
        actionInformation.setValues(values);
        actionInformation.setServiceInterface("serviceInterface");
        actionInformation.setServiceMethod("serviceMethod");

        return actionInformation;
    }
    private ActionEvent prepareActionEvent() {
        ActionEvent actionEvent = new ActionEvent();
        actionEvent.setDisplayName("Action");
        actionEvent.setSubject("actionSubject");
        actionEvent.setDescription("");

        SortedSet<ActionParameter> parameters = new TreeSet<>();
        ActionParameter parameter = new ActionParameter();
        parameter.setDisplayName("Map");
        parameter.setKey("map");
        parameter.setType(MAP);
        parameter.setOrder(1);
        parameters.add(parameter);
        actionEvent.setActionParameters(parameters);
        actionEvent.setPostActionParameters(parameters);

        return actionEvent;
    }

    private ActionEvent prepareActionEventWithService() {
        ActionEvent actionEvent = new ActionEventBuilder().setDisplayName("Action")
                .setDescription("").setServiceInterface("serviceInterface").setServiceMethod("serviceMethod").build();

        SortedSet<ActionParameter> parameters = new TreeSet<>();
        ActionParameter parameter = new ActionParameter();
        parameter.setDisplayName("test");
        parameter.setKey("testKey");
        parameter.setType(TEXTAREA);
        parameter.setOrder(0);
        parameters.add(parameter);
        actionEvent.setActionParameters(parameters);
        actionEvent.setPostActionParameters(parameters);

        return actionEvent;
    }

    private class TestService {

        private boolean invoked;

        private boolean serviceMethodInvoked() {
            return invoked;
        }

        public void serviceMethod() {
            invoked = true;
        }

        public Object serviceMethod(String string) {
            invoked = true;
            return new ObjectTest("testObject");
        }
    }
}
