package org.motechproject.server.asterisk.callback;

import junitx.util.PrivateAccessor;
import org.asteriskjava.live.AsteriskChannel;
import org.asteriskjava.live.CallDetailRecord;
import org.asteriskjava.live.LiveException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.MotechEvent;
import org.motechproject.ivr.service.CallRequest;

import static org.mockito.Mockito.*;

class MyServerEventRelay implements EventRelay {
    @Override
    public void sendEventMessage(MotechEvent motechEvent) {

    }
}

@RunWith(MockitoJUnitRunner.class)
public class MotechAsteriskCallBackImplTest {

    @Mock
    private MyServerEventRelay eventRelay;

    @Mock
    private AsteriskChannel asteriskChannel;

    @Mock
    private LiveException liveException;

    @Mock
    private CallDetailRecord asteriskCallDetailRecord;

    MotechEvent event;
    CallRequest callRequest;
    MotechAsteriskCallBackImpl motechAsteriskCallBack;

    @Before
    public void setUp() throws Exception {
        event = new MotechEvent("", null);

        callRequest = new CallRequest("", 0, "http://localhost");

        motechAsteriskCallBack = new MotechAsteriskCallBackImpl(callRequest);

        when(asteriskChannel.getCallDetailRecord()).thenReturn(asteriskCallDetailRecord);

        PrivateAccessor.setField(motechAsteriskCallBack, "eventRelay", eventRelay);
    }

    @Test
    public void testOnBusy_Event() throws Exception {
        callRequest.setOnBusyEvent(event);

        motechAsteriskCallBack.onBusy(asteriskChannel);

        verify(eventRelay, times(1)).sendEventMessage(event);
    }

    @Test
    public void testOnBusy_NoEvent() throws Exception {
        motechAsteriskCallBack.onBusy(asteriskChannel);

        verify(eventRelay, times(0)).sendEventMessage(event);
    }

    @Test
    public void testOnSuccess_Event() throws Exception {
        callRequest.setOnSuccessEvent(event);

        motechAsteriskCallBack.onSuccess(asteriskChannel);

        verify(eventRelay, times(1)).sendEventMessage(event);
    }

    @Test
    public void testOnSuccess_NoEvent() throws Exception {
        motechAsteriskCallBack.onSuccess(asteriskChannel);

        verify(eventRelay, times(0)).sendEventMessage(event);
    }

    @Test
    public void testOnNoAnswer_Event() throws Exception {
        callRequest.setOnNoAnswerEvent(event);

        motechAsteriskCallBack.onNoAnswer(asteriskChannel);

        verify(eventRelay, times(1)).sendEventMessage(event);
    }

    @Test
    public void testOnNoAnswer_NoEvent() throws Exception {
        motechAsteriskCallBack.onNoAnswer(asteriskChannel);

        verify(eventRelay, times(0)).sendEventMessage(event);
    }

    @Test
    public void testOnFailure_Event() throws Exception {
        callRequest.setOnFailureEvent(event);

        motechAsteriskCallBack.onFailure(liveException);

        verify(eventRelay, times(1)).sendEventMessage(event);
    }

    @Test
    public void testOnFailure_NoEvent() throws Exception {
        motechAsteriskCallBack.onFailure(liveException);

        verify(eventRelay, times(0)).sendEventMessage(event);
    }
}
