package org.motechproject.admin.messages;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.admin.domain.StatusMessage;
import org.motechproject.admin.repository.AllStatusMessages;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.admin.service.impl.StatusMessageServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class StatusMessageServiceTest {

    @InjectMocks
    StatusMessageService statusMessageService = new StatusMessageServiceImpl();

    @Mock
    AllStatusMessages allStatusMessages;

    Level level = Level.INFO;

    StatusMessage activeMessage = new StatusMessage("active", Level.INFO, DateTime.now().plusHours(1));
    StatusMessage inactiveMessage = new StatusMessage("inactive", Level.INFO, DateTime.now().minusHours(1));

    List<StatusMessage> statusMessages = new ArrayList<>();

    @Before
    public void setUp() {
        initMocks(this);
        statusMessages.add(activeMessage);
        statusMessages.add(inactiveMessage);
    }

    @Test
    public void testGetAllMessages() {
        when(allStatusMessages.getAll()).thenReturn(statusMessages);

        List<StatusMessage> result = statusMessageService.getAllMessages();

        assertEquals(statusMessages, result);
        verify(allStatusMessages).getAll();
    }

    @Test
    public void testActiveMessages() {
        when(allStatusMessages.getAll()).thenReturn(statusMessages);

        List<StatusMessage> result = statusMessageService.getActiveMessages();

        assertEquals(1, result.size());
        assertEquals(activeMessage ,result.get(0));
        verify(allStatusMessages).getAll();
    }

    @Test
    public void testPostMessage() {
        statusMessageService.postMessage(activeMessage);
        verify(allStatusMessages).add(activeMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostMessageNullText() {
        StatusMessage illegalMessage = new StatusMessage(null, Level.INFO);
        statusMessageService.postMessage(illegalMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostMessageNullTimeout() {
        StatusMessage illegalMessage = new StatusMessage("text", Level.INFO, null);
        statusMessageService.postMessage(illegalMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostMessagePastTimeout() {
        statusMessageService.postMessage(inactiveMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPostMessageNullLevel() {
        StatusMessage illegal = new StatusMessage("text", null);
        statusMessageService.postMessage(illegal);
    }
}
