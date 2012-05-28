package org.motechproject.ivr.kookoo.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.domain.CallSessionRecord;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.KooKooIVRContextForTest;
import org.motechproject.ivr.kookoo.KookooCallbackRequest;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.extensions.CallFlowController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.ivr.service.IVRSessionManagementService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRControllerTest {
    @Mock
    KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    private CallFlowController callFlowController;
    private IVRController ivrController;
    private KooKooIVRContextForTest ivrContextForTest;
    @Mock
    private IVRSessionManagementService ivrSessionManagementService;

    @Before
    public void setUp() {
        initMocks(this);
        ivrController = new IVRController(callFlowController, callDetailRecordsService, ivrSessionManagementService);
    }

    @Test
    public void shouldCreateNewCallDetailRecord_WhenNoRecordHasBeenCreated() {
        String callerId = "98675";
        CallDirection callDirection = CallDirection.Inbound;
        String callId = "123";
        String kooKooCallDetailRecordId = "32432545";
        String treeName = "fooTree";

        ivrContextForTest = new KooKooIVRContextForTest().callerId(callerId).ivrEvent(IVREvent.NewCall).callDirection(callDirection);
        ivrContextForTest.callId(callId);
        when(callFlowController.urlFor(ivrContextForTest)).thenReturn(AllIVRURLs.DECISION_TREE_URL);
        when(callFlowController.decisionTreeName(ivrContextForTest)).thenReturn(treeName);
        when(callDetailRecordsService.createAnsweredRecord(callId, callerId, callDirection)).thenReturn(kooKooCallDetailRecordId);

        ivrController.reply(ivrContextForTest);
        assertEquals(kooKooCallDetailRecordId, ivrContextForTest.callDetailRecordId());
        assertEquals(treeName, ivrContextForTest.treeName());
    }
    
    @Test
    public void shouldHandleCallSessionRecordForEveryRequest() {
        KookooRequest kookooRequest = mock(KookooRequest.class);
        when(kookooRequest.getSid()).thenReturn("sid");
        when(kookooRequest.getEvent()).thenReturn(IVREvent.NewCall.name());

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getCookies()).thenReturn(new Cookie[]{});
        when(request.getSession()).thenReturn(session);

        when(ivrSessionManagementService.getCallSession("sid")).thenReturn(new CallSessionRecord("sid"));
        doNothing().when(callDetailRecordsService).appendToLastCallEvent(anyString(), anyMap());

        ivrController.reply(kookooRequest, request, mock(HttpServletResponse.class));
        verify(ivrSessionManagementService, times(1)).getCallSession("sid");
        verify(ivrSessionManagementService, times(1)).updateCallSession(any(CallSessionRecord.class));
    }

    @Test
    public void shouldAppendNewCallEventToCallDetailRecord_WhenRecordExists() {
        String callerId = "98675";
        CallDirection callDirection = CallDirection.Inbound;
        String callId = "123";
        String kooKooCallDetailRecordId = "32432545";
        String treeName = "fooTree";

        ivrContextForTest = new KooKooIVRContextForTest().callerId(callerId).ivrEvent(IVREvent.NewCall).callDirection(callDirection);
        ivrContextForTest.callId(callId);
        ivrContextForTest.callDetailRecordId(kooKooCallDetailRecordId);
        when(callFlowController.urlFor(ivrContextForTest)).thenReturn(AllIVRURLs.DECISION_TREE_URL);
        when(callFlowController.decisionTreeName(ivrContextForTest)).thenReturn(treeName);

        ivrController.reply(ivrContextForTest);

        verify(callDetailRecordsService).setCallRecordAsAnswered(ivrContextForTest.callId(), ivrContextForTest.callDetailRecordId());
        verify(callDetailRecordsService, never()).createAnsweredRecord(ivrContextForTest.callId(), ivrContextForTest.callerId(), ivrContextForTest.callDirection());
        ArgumentCaptor<IVREvent> ivrEventArgumentCaptor = ArgumentCaptor.forClass(IVREvent.class);
        verify(callDetailRecordsService).appendEvent(eq(ivrContextForTest.callDetailRecordId()), ivrEventArgumentCaptor.capture(), eq(""));
        assertTrue(ivrEventArgumentCaptor.getValue().equals(IVREvent.NewCall));
    }

    @Test
    public void disconnectShouldInvalidateSessionAndCloseCallRecord_ForAValidSession() {
        String externalId = "455345";
        String callDetailRecordId = "4324234";
        ivrContextForTest = new KooKooIVRContextForTest().externalId(externalId).ivrEvent(IVREvent.Disconnect);
        ivrContextForTest.callDetailRecordId(callDetailRecordId);
        when(ivrSessionManagementService.isValidSession(ivrContextForTest.callId())).thenReturn(true);

        ivrController.reply(ivrContextForTest);

        verify(ivrSessionManagementService).removeCallSession(ivrContextForTest.callId());

        ArgumentCaptor<CallEvent> callEventArgumentCaptor = ArgumentCaptor.forClass(CallEvent.class);
        verify(callDetailRecordsService).close(eq(callDetailRecordId), eq(externalId), callEventArgumentCaptor.capture());
        assertEquals(IVREvent.Disconnect.toString(), callEventArgumentCaptor.getValue().getName());
    }

    @Test
    public void disconnectShouldNotInvalidateSessionAndCloseCallRecord_ForAnInvalidSession() {
        String externalId = "455345";
        String callDetailRecordId = "4324234";
        ivrContextForTest = new KooKooIVRContextForTest().externalId(externalId).ivrEvent(IVREvent.Disconnect);
        ivrContextForTest.callDetailRecordId(callDetailRecordId);
        when(ivrSessionManagementService.isValidSession(ivrContextForTest.callId())).thenReturn(false);
        ivrController.reply(ivrContextForTest);
        verify(ivrSessionManagementService, times(0)).removeCallSession(ivrContextForTest.callId());
        verify(callDetailRecordsService, times(0)).close(callDetailRecordId, externalId, new CallEvent(IVREvent.Disconnect.toString()));
    }

    @Test
    public void shouldRedirectToDialControllerURLWhenDialEventIsEncountered() {
        ivrContextForTest = new KooKooIVRContextForTest().ivrEvent(IVREvent.Dial);
        when(callFlowController.urlFor(ivrContextForTest)).thenReturn("/ivr/dial");

        String replyURL = ivrController.reply(ivrContextForTest);
        assertEquals("forward:/ivr/dial/dial", replyURL);
    }

    @Test
    public void shouldNotRecordCallDetail_OnKookooCallback_IfCallWasAnswered() {
        KookooCallbackRequest kookooCallbackRequest = new KookooCallbackRequest();
        kookooCallbackRequest.setStatus("answered");

        ivrController.callback(kookooCallbackRequest);
        verifyZeroInteractions(callDetailRecordsService);
    }

    @Test
    public void shouldAppendMissedCallEventToCallDetail_OnKookooCallback_IfCallWasNotAnswered() {
        ivrContextForTest = new KooKooIVRContextForTest();
        String call_detail_record_id = "1234";
        ivrContextForTest.callDetailRecordId(call_detail_record_id);
        KookooCallbackRequest kookooCallbackRequest = new KookooCallbackRequest();
        kookooCallbackRequest.setStatus("ring");
        kookooCallbackRequest.setPhone_no("phone_no");
        kookooCallbackRequest.setCall_type("outbox");
        kookooCallbackRequest.setExternal_id("external_id");
        kookooCallbackRequest.setCall_detail_record_id(call_detail_record_id);

        ivrController.callback(kookooCallbackRequest);
        verify(callDetailRecordsService).setCallRecordAsNotAnswered(call_detail_record_id);
        verify(callDetailRecordsService, never()).createOutgoing(eq("phone_no"), eq(CallDetailRecord.Disposition.NO_ANSWER));

        ArgumentCaptor<CallEvent> callEventArgumentCaptor = ArgumentCaptor.forClass(CallEvent.class);
        verify(callDetailRecordsService).close(anyString(), eq("external_id"), callEventArgumentCaptor.capture());
        assertEquals(IVREvent.Missed.toString(), callEventArgumentCaptor.getValue().getName());
        assertEquals("outbox", callEventArgumentCaptor.getValue().getData().getFirst(IVRService.CALL_TYPE));
    }
}
