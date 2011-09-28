package org.motechproject.ivr.kookoo.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.action.UserNotFoundAction;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.ivr.kookoo.service.UserService;
import org.motechproject.server.service.ivr.IVRCallState;
import org.motechproject.server.service.ivr.IVRMessage;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;


public class NewCallEventActionTest extends BaseActionTest {

    private NewCallEventAction action;
    @Mock
    private UserService userService;
    @Mock
    private IVRMessage ivrMessages;
    @Mock
    private UserNotFoundAction userNotFoundAction;
    @Mock
    private AllKooKooCallDetailRecords allCallDetailRecords;

    @Before
    public void setUp() {
        super.setUp();
        action = new NewCallEventAction(ivrMessages, userNotFoundAction, userService, eventService, callIdentifiers, allCallDetailRecords);
    }

    @Test
    public void shouldAskUserNotFoundActionToHandleIfUserIsNotRegistered() {
        IVRRequest ivrRequest = new KookooRequest();
        when(userService.isRegisteredUser(ivrRequest.getCid())).thenReturn(false);

        action.handle(ivrRequest, request, response);
        verify(userNotFoundAction).handle(ivrRequest, request, response);
    }

    @Test
    public void shouldLogNewCallEvent_ForIncomingCall() {
        IVRRequest ivrRequest = new KookooRequest();
        String callId = "callId";
        String callerId = "callerId";

        ivrRequest.setSid(callId);
        ivrRequest.setCid(callerId);
        Mockito.when(userService.isRegisteredUser(callerId)).thenReturn(true);

        action.handle(ivrRequest, request, response);

        ArgumentCaptor<KookooCallDetailRecord> callDetailRecordCapture = ArgumentCaptor.forClass(KookooCallDetailRecord.class);
        verify(allCallDetailRecords).add(callDetailRecordCapture.capture());

        KookooCallDetailRecord capturedCallDetailRecord = callDetailRecordCapture.getValue();
        assertEquals(callId, capturedCallDetailRecord.getCallDetailRecord().getCallId());
        assertEquals(IVRRequest.CallDirection.Inbound, capturedCallDetailRecord.getCallDetailRecord().getCallDirection());
    }

    @Test
    public void shouldLogNewCallEvent_ForOutgoingCall() {
        IVRRequest ivrRequest = new KookooRequest();
        String callId = "callId";
        String callerId = "callerId";

        ivrRequest.setSid(callId);
        ivrRequest.setCid(callerId);
        ivrRequest.setParameter(IVRSession.IVRCallAttribute.IS_OUTBOUND_CALL, "true");
        Mockito.when(userService.isRegisteredUser(callerId)).thenReturn(true);

        action.handle(ivrRequest, request, response);

        ArgumentCaptor<KookooCallDetailRecord> callDetailRecordCapture = ArgumentCaptor.forClass(KookooCallDetailRecord.class);
        verify(allCallDetailRecords).add(callDetailRecordCapture.capture());

        KookooCallDetailRecord capturedCallDetailRecord = callDetailRecordCapture.getValue();
        assertEquals(callId, capturedCallDetailRecord.getCallDetailRecord().getCallId());
        assertEquals(IVRRequest.CallDirection.Outbound, capturedCallDetailRecord.getCallDetailRecord().getCallDirection());
    }

    @Test
    public void shouldLogNewCallEvent_WhenUserIsNotRegistered() {
        IVRRequest ivrRequest = new KookooRequest();
        String callId = "callId";
        String callerId = "callerId";

        ivrRequest.setSid(callId);
        ivrRequest.setCid(callerId);
        Mockito.when(userService.isRegisteredUser(callerId)).thenReturn(false);

        action.handle(ivrRequest, request, response);

        ArgumentCaptor<KookooCallDetailRecord> callDetailRecordCapture = ArgumentCaptor.forClass(KookooCallDetailRecord.class);
        verify(allCallDetailRecords).add(callDetailRecordCapture.capture());

        KookooCallDetailRecord capturedCallDetailRecord = callDetailRecordCapture.getValue();
        assertEquals(callId, capturedCallDetailRecord.getCallDetailRecord().getCallId());
    }

    @Test
    public void shouldSetAttributesInSessionAndSendDtmfResponseWithWav() {
        IVRRequest ivrRequest = new KookooRequest();
        when(userService.isRegisteredUser(ivrRequest.getCid())).thenReturn(true);
        when(request.getSession()).thenReturn(session);

        String xmlResponse = action.handle(ivrRequest, request, response);
        verify(session).setAttribute(IVRSession.IVRCallAttribute.CALLER_ID, ivrRequest.getCid());
        verify(session).setAttribute(IVRSession.IVRCallAttribute.CALL_STATE, IVRCallState.COLLECT_PIN);
        assertEquals("<response><collectdtmf><playaudio/></collectdtmf></response>", sanitize(xmlResponse));
    }
}
