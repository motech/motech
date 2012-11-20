package org.motechproject.decisiontree.server.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.EndOfCallEvent;
import org.motechproject.decisiontree.core.model.CallStatus;
import org.motechproject.decisiontree.server.domain.FlowSessionRecord;
import org.motechproject.event.listener.EventRelay;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DecisionTreeServerImplTest {

    DecisionTreeServer decisionTreeServer;

    @Mock
    private DecisionTreeService decisionTreeService;
    @Mock
    private TreeEventProcessor treeEventProcessor;
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private FlowSessionService flowSessionService;
    @Mock
    private EventRelay eventRelay;

    @Before
    public void setup() {
        initMocks(this);
        decisionTreeServer = new DecisionTreeServerImpl(decisionTreeService, treeEventProcessor, applicationContext, flowSessionService, eventRelay);
    }

    @Test
    public void shouldRaiseEndOfCallEventOnHangup() {
        FlowSessionRecord flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        decisionTreeServer.getResponse("123a", "1234567890", "freeivr", "sometree", CallStatus.Hangup.toString(), "en");

        verify(eventRelay).sendEventMessage(new EndOfCallEvent(flowSession.getCallDetailRecord()));
    }

    @Test
    public void shouldRaiseEndOfCallEventOnDisconnect() {
        FlowSessionRecord flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        decisionTreeServer.getResponse("123a", "1234567890", "freeivr", "sometree", CallStatus.Disconnect.toString(), "en");

        verify(eventRelay).sendEventMessage(new EndOfCallEvent(flowSession.getCallDetailRecord()));
    }

    @Test
    public void shouldRaiseEndOfCallEventForMissedCalls() {
        FlowSessionRecord flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.getSession("123a")).thenReturn(flowSession);

        decisionTreeServer.handleMissedCall(flowSession.getSessionId());
        verify(flowSessionService).updateSession(flowSession);
        verify(eventRelay).sendEventMessage(new EndOfCallEvent(flowSession.getCallDetailRecord()));
    }

    @Test
    public void testingCallDetailRecordParams() {
        FlowSessionRecord flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        decisionTreeServer.getResponse("123a", "1234567890", "freeivr", "sometree", CallStatus.Disconnect.toString(), "en");

        verify(eventRelay).sendEventMessage(new EndOfCallEvent(flowSession.getCallDetailRecord()));
    }
}
