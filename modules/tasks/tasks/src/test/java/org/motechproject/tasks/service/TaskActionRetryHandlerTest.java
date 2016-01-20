package org.motechproject.tasks.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventListener;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.events.constants.EventDataKeys;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TaskActionRetryHandlerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private EventListenerRegistryService registryService;

    @Mock
    private EventListener eventListener;

    @Mock
    private Task task;

    private TaskActionRetryHandler taskActionRetryHandler;

    @Before
    public void setUp() throws Exception {
        taskActionRetryHandler = new TaskActionRetryHandler();
        taskActionRetryHandler.setTaskService(taskService);
        taskActionRetryHandler.setRegistryService(registryService);
    }

    @Test
    public void shouldThrowExceptionFromMotechEventWhenNumberOfRetriesIsZero() throws InterruptedException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EventDataKeys.TASK_ID, 2L);

        MotechEvent motechEvent = new MotechEvent("Subject", parameters);
        motechEvent.setExceptionFromListener(new RuntimeException("ExampleException"));

        when(taskService.getTask(2L)).thenReturn(task);
        when(task.getNumberOfRetries()).thenReturn(0);
        when(task.getRetryIntervalInMilliseconds()).thenReturn(0);

        String message = "";
        try {
            taskActionRetryHandler.handleRetries(motechEvent);
        } catch (RuntimeException ex) {
            message = ex.getMessage();
        }

        assertEquals("ExampleException", message);
        verifyZeroInteractions(registryService);
    }

    @Test
    public void shouldRetryActionAfterFailureWhenValidTaskRetryCountValueIsSet() throws InterruptedException {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(EventDataKeys.TASK_ID, 2L);

        MotechEvent motechEvent = new MotechEvent("Subject", parameters);
        motechEvent.setRetryHandlerSubject("RetryHandleSubject");
        motechEvent.setMessageDestination("MessageDestination");

        when(taskService.getTask(2L)).thenReturn(task);
        when(task.getNumberOfRetries()).thenReturn(5);
        when(task.getRetryIntervalInMilliseconds()).thenReturn(0);

        when(registryService.getListeners(motechEvent.getSubject())).thenReturn(new HashSet<>(Arrays.asList(eventListener)));
        when(eventListener.getIdentifier()).thenReturn(motechEvent.getMessageDestination());
        doThrow(new RuntimeException("ExampleException")).when(eventListener).handle(motechEvent);

        String message = "";
        try {
            taskActionRetryHandler.handleRetries(motechEvent);
        } catch (RuntimeException ex) {
            message = ex.getMessage();
        }

        assertEquals("ExampleException", message);
        verify(eventListener, times(5)).handle(motechEvent);
    }
}
