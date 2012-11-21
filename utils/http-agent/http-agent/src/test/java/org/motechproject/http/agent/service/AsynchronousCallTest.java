package org.motechproject.http.agent.service;

import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.http.agent.components.AsynchronousCall;
import org.motechproject.event.MotechEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class AsynchronousCallTest {

    @Mock
    private EventRelay mockEventRelay;

    private AsynchronousCall asynchronousCall;

    @Test
    public void shouldSendThroughEventRelay() throws Exception {
        initMocks(this);
        asynchronousCall = new AsynchronousCall(mockEventRelay);
        MotechEvent motechEvent = new MotechEvent("subject");
        asynchronousCall.send(motechEvent);

        verify(mockEventRelay).sendEventMessage(motechEvent);
    }
}
