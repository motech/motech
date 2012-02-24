package org.motechproject.ivr.kookoo.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.KooKooIVRContextForTest;
import org.motechproject.ivr.kookoo.KookooCallbackRequest;
import org.motechproject.ivr.kookoo.extensions.CallFlowController;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRControllerTest {
    @Mock
    KookooCallDetailRecordsService callDetailRecordsService;
    @Mock
    private CallFlowController callFlowController;
    private IVRController ivrController;
    private KooKooIVRContextForTest ivrContextForTest;

    @Before
    public void setUp() {
        initMocks(this);
        ivrController = new IVRController(callFlowController, callDetailRecordsService);
    }

    @Test
    public void newCallShouldCreateNewCallDetailRecord() {
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
    public void disconnectShouldInvalidateSessionAndCloseCallRecord_ForAValidSession() {
        String externalId = "455345";
        String callDetailRecordId = "4324234";
        ivrContextForTest = new KooKooIVRContextForTest().externalId(externalId).ivrEvent(IVREvent.Disconnect);
        ivrContextForTest.callDetailRecordId(callDetailRecordId);
        ivrContextForTest.isValidSession(true);
        ivrController.reply(ivrContextForTest);
        assertEquals(true, ivrContextForTest.sessionInvalidated());
        verify(callDetailRecordsService).close(callDetailRecordId, externalId, IVREvent.Disconnect);
    }

    @Test
    public void disconnectShouldNotInvalidateSessionAndCloseCallRecord_ForAnInvalidSession() {
        String externalId = "455345";
        String callDetailRecordId = "4324234";
        ivrContextForTest = new KooKooIVRContextForTest().externalId(externalId).ivrEvent(IVREvent.Disconnect);
        ivrContextForTest.callDetailRecordId(callDetailRecordId);
        ivrContextForTest.isValidSession(false);
        ivrController.reply(ivrContextForTest);
        assertEquals(false, ivrContextForTest.sessionInvalidated());
        verify(callDetailRecordsService, times(0)).close(callDetailRecordId, externalId, IVREvent.Disconnect);
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
    public void shouldRecordCallDetail_OnKookooCallback_IfCallWasNotAnswered() {
        KookooCallbackRequest kookooCallbackRequest = new KookooCallbackRequest();
        kookooCallbackRequest.setStatus("ring");
        kookooCallbackRequest.setPhone_no("phone_no");
        kookooCallbackRequest.setExternal_id("external_id");

        ivrController.callback(kookooCallbackRequest);
        verify(callDetailRecordsService).createOutgoing(anyString(), eq("phone_no"), eq(CallDetailRecord.Disposition.NO_ANSWER));
        verify(callDetailRecordsService).close(anyString(), eq("external_id"), eq(IVREvent.Missed));
    }
}
