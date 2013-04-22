package org.motechproject.admin.messages;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.motechproject.admin.events.EventKeys;
import org.motechproject.admin.events.EventSubjects;
import org.motechproject.admin.service.StatusMessageService;
import org.motechproject.event.MotechEvent;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

public class MessageHandlerTest {

    @InjectMocks
    private MessageHandler messageHandler = new MessageHandler();

    @Mock
    private StatusMessageService statusMessageService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldPostMessageWithTimeout() {
        final DateTime timeout = DateTime.now();

        Map<String, Object> params = new HashMap<>();
        params.put(EventKeys.MESSAGE, "testMsg");
        params.put(EventKeys.MODULE_NAME, "testModule");
        params.put(EventKeys.LEVEL, "info");
        params.put(EventKeys.TIMEOUT, timeout);

        MotechEvent event = new MotechEvent(EventSubjects.MESSAGE_SUBJECT, params);

        messageHandler.messageReceived(event);

        verify(statusMessageService).postMessage("testMsg", "testModule", Level.INFO, timeout);
    }

    @Test
    public void shouldPostMessageWithoutTimeout() {
        Map<String, Object> params = new HashMap<>();
        params.put(EventKeys.MESSAGE, "testMsg");
        params.put(EventKeys.MODULE_NAME, "testModule");
        params.put(EventKeys.LEVEL, "warn");

        MotechEvent event = new MotechEvent(EventSubjects.MESSAGE_SUBJECT, params);

        messageHandler.messageReceived(event);

        verify(statusMessageService).postMessage("testMsg", "testModule", Level.WARN);
    }
}
