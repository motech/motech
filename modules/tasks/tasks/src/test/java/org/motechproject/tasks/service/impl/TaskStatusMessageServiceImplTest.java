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
        Task t = new Task();
        t.setId("12345");

        List<TaskStatusMessage> messages = new ArrayList<>();
        messages.add(createError(t));
        messages.add(createError(t));
        messages.add(createSuccess(t));
        messages.add(createError(t));
        messages.add(createError(t));
        messages.add(createWarning(t));
        messages.add(createSuccess(t));
        messages.add(createError(t));
        messages.add(createError(t));
        messages.add(createError(t));
        messages.add(createError(t));

        when(allTaskStatusMessages.byTaskId(t.getId())).thenReturn(messages);

        List<TaskStatusMessage> errors = messageService.errorsFromLastRun(t);

        assertNotNull(errors);
        assertEquals(4, errors.size());

        for (TaskStatusMessage error : errors) {
            assertEquals(ERROR.getValue(), error.getMessage());
            assertEquals(t.getId(), error.getTask());
            assertEquals(ERROR, error.getLevel());
        }
    }

    private TaskStatusMessage createError(final Task t) {
        return new TaskStatusMessage(ERROR.getValue(), t.getId(), ERROR);
    }

    private TaskStatusMessage createSuccess(final Task t) {
        return new TaskStatusMessage(SUCCESS.getValue(), t.getId(), SUCCESS);
    }

    private TaskStatusMessage createWarning(final Task t) {
        return new TaskStatusMessage(WARNING.getValue(), t.getId(), WARNING);
    }
}
