package org.motechproject.tasks.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.ActionEvent;
import org.motechproject.tasks.domain.ActionParameter;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskBuilder;
import org.motechproject.tasks.ex.ActionNotFoundException;
import org.motechproject.tasks.ex.TaskHandlerException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.HashMap;
import java.util.TreeSet;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskActionExecutorTest {

    @Mock
    private TaskService taskService;
    @Mock
    private TaskActivityService activityService;
    @Mock
    private EventRelay eventRelay;
    @Mock
    private BundleContext bundleContext;

    @Test
    public void shouldRaiseEventIfActionHasSubject() throws ActionNotFoundException, TaskHandlerException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "actionSubject");
        ActionEvent actionEvent = new ActionEvent("Action", "actionSubject", "", new TreeSet<ActionParameter>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        TaskActionExecutor taskActionExecutor = new TaskActionExecutor(taskService, activityService, eventRelay);
        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, new TaskContext(task, new MotechEvent("trigger"), activityService));

        MotechEvent raisedEvent = new MotechEvent("actionSubject", new HashMap<String, Object>());
        verify(eventRelay).sendEventMessage(raisedEvent);
    }

    @Test
    public void shouldRaiseEventWhenActionHasSubjectAndService_IfServiceIsNotAvailable() throws TaskHandlerException, ActionNotFoundException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEvent("Action", "actionSubject", "", "serviceInterface", "serviceMethod", new TreeSet<ActionParameter>());
        actionEvent.setActionParameters(new TreeSet<ActionParameter>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(null);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        TaskActionExecutor taskActionExecutor = new TaskActionExecutor(taskService, activityService, eventRelay);
        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, new TaskContext(task, new MotechEvent("test"), activityService));

        verify(eventRelay).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldNotRaiseEventIfActionHasSubjectAndService_IfServiceIsAvailable() throws ActionNotFoundException, TaskHandlerException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEvent("Action", "actionSubject", "", "serviceInterface", "serviceMethod", new TreeSet<ActionParameter>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        ServiceReference serviceReference = mock(ServiceReference.class);
        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(serviceReference);
        when(bundleContext.getService(serviceReference)).thenReturn(new TestService());

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        TaskActionExecutor taskActionExecutor = new TaskActionExecutor(taskService, activityService, eventRelay);
        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, new TaskContext(task, new MotechEvent("trigger"), activityService));

        verify(eventRelay, never()).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldInvokeServiceIfActionHasService() throws ActionNotFoundException, TaskHandlerException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEvent("Action", "", "serviceInterface", "serviceMethod", new TreeSet<ActionParameter>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        ServiceReference serviceReference = mock(ServiceReference.class);
        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(serviceReference);
        TestService testService = new TestService();
        when(bundleContext.getService(serviceReference)).thenReturn(testService);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        TaskActionExecutor taskActionExecutor = new TaskActionExecutor(taskService, activityService, eventRelay);
        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, new TaskContext(task, new MotechEvent("trigger"), activityService));

        assertTrue(testService.serviceMethodInvoked());
    }

    @Test(expected = TaskHandlerException.class)
    public void shouldThrowExceptionIfBundleContextIsNotAvailable() throws TaskHandlerException, ActionNotFoundException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEvent("Action", "", "serviceInterface", "serviceMethod", new TreeSet<ActionParameter>());
        actionEvent.setActionParameters(new TreeSet<ActionParameter>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        TaskActionExecutor taskActionExecutor = new TaskActionExecutor(taskService, activityService, eventRelay);

        taskActionExecutor.execute(task, actionInformation, new TaskContext(task, new MotechEvent("test"), activityService));
    }

    @Test(expected = TaskHandlerException.class)
    public void shouldThrowExceptionIfActionHasNeitherEventNorService() throws TaskHandlerException, ActionNotFoundException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEvent("Action", "", "serviceInterface", "serviceMethod", new TreeSet<ActionParameter>());
        actionEvent.setActionParameters(new TreeSet<ActionParameter>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        TaskActionExecutor taskActionExecutor = new TaskActionExecutor(taskService, activityService, eventRelay);

        taskActionExecutor.execute(task, actionInformation, new TaskContext(task, new MotechEvent("test"), activityService));
    }

    @Test
    public void shouldAddActivityNotificationIfServiceIsNotAvailable() throws TaskHandlerException, ActionNotFoundException {
        TaskActionInformation actionInformation = new TaskActionInformation("action", "channel", "module", "0.1", "serviceInterface", "serviceMethod");
        ActionEvent actionEvent = new ActionEvent("Action", "actionSubject", "", "serviceInterface", "serviceMethod", new TreeSet<ActionParameter>());
        actionEvent.setActionParameters(new TreeSet<ActionParameter>());
        when(taskService.getActionEventFor(actionInformation)).thenReturn(actionEvent);

        when(bundleContext.getServiceReference("serviceInterface")).thenReturn(null);

        Task task = new TaskBuilder().addAction(new TaskActionInformation("Action", "channel", "module", "0.1", "actionSubject")).build();

        TaskActionExecutor taskActionExecutor = new TaskActionExecutor(taskService, activityService, eventRelay);
        taskActionExecutor.setBundleContext(bundleContext);

        taskActionExecutor.execute(task, actionInformation, new TaskContext(task, new MotechEvent("test"), activityService));

        verify(activityService).addWarning(task, "task.warning.serviceUnavailable", "serviceInterface");
    }

    private class TestService {

        private boolean invoked;

        private boolean serviceMethodInvoked() {
            return invoked;
        }

        public void serviceMethod() {
            invoked = true;
        }
    }
}
