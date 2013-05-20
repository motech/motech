package org.motechproject.callflow.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.callflow.domain.FlowSessionRecord;
import org.motechproject.callflow.domain.IvrEvent;
import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.ivr.domain.CallEvent;
import org.springframework.context.ApplicationContext;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;
import static org.motechproject.testing.utils.TimeFaker.fakeNow;
import static org.motechproject.testing.utils.TimeFaker.stopFakingTime;

public class CallFlowServerImplTest {

    CallFlowServer callFlowServer;

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
        callFlowServer = new CallFlowServerImpl(decisionTreeService, treeEventProcessor, applicationContext, flowSessionService, eventRelay);
    }

    @Test
    public void shouldRaiseCallEvent() {
        FlowSessionRecord flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.getSession("123a")).thenReturn(flowSession);

        callFlowServer.raiseCallEvent(IvrEvent.Answered, flowSession.getSessionId());

        verify(eventRelay).sendEventMessage(new CallEvent(IvrEvent.Answered.getEventSubject(), flowSession.getCallDetailRecord()).toMotechEvent());
    }

    @Test
    public void shouldSetCallEndTimeWhenCallEnds() {
        try {
            fakeNow(newDateTime(2010, 10, 1));

            FlowSessionRecord flowSession = new FlowSessionRecord("123a", "1234567890");
            when(flowSessionService.getSession("123a")).thenReturn(flowSession);

            callFlowServer.raiseCallEvent(IvrEvent.Answered, flowSession.getSessionId());

            ArgumentCaptor<FlowSession> sessionCaptor = ArgumentCaptor.forClass(FlowSession.class);
            verify(flowSessionService).updateSession(sessionCaptor.capture());

            FlowSessionRecord sessionRecord = (FlowSessionRecord) sessionCaptor.getValue();
            assertEquals(newDateTime(2010, 10, 1), sessionRecord.getCallDetailRecord().getEndDate());
        } finally {
            stopFakingTime();
        }
    }
}
