package org.motechproject.ivr.kookoo.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.EndOfCallEvent;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class HangupEventActionTest extends BaseActionTest {

    private HangupEventAction action;

    private IVRRequest ivrRequest;

    @Mock
    private KookooCallDetailRecord kooKooCallDetailRecord;

    @Mock
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    @Mock
    private EventService eventService;


    @Before
    public void setUp() {
        super.setUp();
        ivrRequest = new KookooRequest();
        ivrRequest.setSid("callId");
        ivrRequest.setCid("callerId");
        ivrRequest.setEvent(IVREvent.NEW_CALL.key());

        action = new HangupEventAction(eventService, allKooKooCallDetailRecords);
        CallDetailRecord callDetailRecord = CallDetailRecord.newIncomingCallRecord("callId", "phoneNumber");
        when(kooKooCallDetailRecord.getCallDetailRecord()).thenReturn(callDetailRecord);
        when(allKooKooCallDetailRecords.findByCallId("callId")).thenReturn(kooKooCallDetailRecord);
        when(session.getAttribute("reference-id")).thenReturn("referenceId");
    }

    @Test
    public void shouldCloseTheRecord() {
        action.handleInternal(ivrRequest, request, response);
        verify(kooKooCallDetailRecord).close();
    }

    @Test
    public void shouldRaiseEndOfCallEvent() {
        action.handleInternal(ivrRequest, request, response);

        ArgumentCaptor<EndOfCallEvent> endOfCallEventArgumentCaptor = ArgumentCaptor.forClass(EndOfCallEvent.class);
        verify(eventService).publishEvent(endOfCallEventArgumentCaptor.capture());
        EndOfCallEvent event = endOfCallEventArgumentCaptor.getValue();
        assertEquals("callId", event.getCallId());
        assertEquals("referenceId", event.getReferenceId());
    }
}
