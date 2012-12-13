package org.motechproject.tasks.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskStatusMessage;
import org.motechproject.tasks.repository.AllTaskStatusMessages;
import org.motechproject.tasks.service.TaskStatusMessageService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.tasks.domain.Level.ERROR;
import static org.motechproject.tasks.domain.Level.SUCCESS;
import static org.motechproject.tasks.domain.Level.WARNING;

public class TaskStatusMessageServiceImplTest {
    private static final String TASK_ID = "12345";

    @Mock
    AllTaskStatusMessages allTaskStatusMessages;

    TaskStatusMessageService messageService;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        messageService = new TaskStatusMessageServiceImpl(allTaskStatusMessages);
    }

    @Test
    public void test_errorsFromLastRun() {
        when(allTaskStatusMessages.byTaskId(TASK_ID)).thenReturn(getTaskStatusMessages());

        Task t = new Task();
        t.setId(TASK_ID);

        List<TaskStatusMessage> errors = messageService.errorsFromLastRun(t);

        assertNotNull(errors);
        assertEquals(4, errors.size());

        for (TaskStatusMessage error : errors) {
            assertEquals(ERROR.getValue(), error.getMessage());
            assertEquals(TASK_ID, error.getTask());
            assertEquals(ERROR, error.getLevel());
        }
    }

    @Test
    public void test_getSuccessMessages() {
        when(allTaskStatusMessages.byTaskId(TASK_ID)).thenReturn(getTaskStatusMessages());

        List<TaskStatusMessage> successes = messageService.getSuccessMessages(TASK_ID);

        assertNotNull(successes);
        assertEquals(2, successes.size());

        for (TaskStatusMessage error : successes) {
            assertEquals(SUCCESS.getValue(), error.getMessage());
            assertEquals(TASK_ID, error.getTask());
            assertEquals(SUCCESS, error.getLevel());
        }
    }

    @Test
    public void test_getErrorMessages() {
        when(allTaskStatusMessages.byTaskId(TASK_ID)).thenReturn(getTaskStatusMessages());

        List<TaskStatusMessage> errors = messageService.getErrorMessages(TASK_ID);

        assertNotNull(errors);
        assertEquals(8, errors.size());

        for (TaskStatusMessage error : errors) {
            assertEquals(ERROR.getValue(), error.getMessage());
            assertEquals(TASK_ID, error.getTask());
            assertEquals(ERROR, error.getLevel());
        }
    }

    private List<TaskStatusMessage> getTaskStatusMessages() {
        List<TaskStatusMessage> messages = new ArrayList<>();
        messages.add(createError());
        messages.add(createError());
        messages.add(createSuccess());
        messages.add(createError());
        messages.add(createError());
        messages.add(createWarning());
        messages.add(createSuccess());
        messages.add(createError());
        messages.add(createError());
        messages.add(createError());
        messages.add(createError());

        return messages;
    }

    private TaskStatusMessage createError() {
        return new TaskStatusMessage(ERROR.getValue(), TASK_ID, ERROR);
    }

    private TaskStatusMessage createSuccess() {
        return new TaskStatusMessage(SUCCESS.getValue(), TASK_ID, SUCCESS);
    }

    private TaskStatusMessage createWarning() {
        return new TaskStatusMessage(WARNING.getValue(), TASK_ID, WARNING);
    }
}
