package org.motechproject.server.kookoo.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.callflow.domain.FlowSessionRecord;
import org.motechproject.callflow.domain.IvrEvent;
import org.motechproject.callflow.service.CallFlowServer;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.DialStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class KookooIvrControllerTest {

    private KookooIvrController kookooIvrController;

    @Mock
    private CallFlowServer callFlowServer;
    @Mock
    private FlowSessionService flowSessionService;

    @Before
    public void setup() {
        initMocks(this);
        kookooIvrController = new KookooIvrController(callFlowServer, flowSessionService);
    }

    @Test
    public void shouldRenderKookooNodeRespone() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("tree", "sometree");
        request.setParameter("ln", "en");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", "sometree", null, "en")).thenReturn(view);

        assertEquals(view, kookooIvrController.ivrCallback(request, new MockHttpServletResponse()));
    }

    @Test
    public void shouldPopulateSessionFields() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("tree", "sometree");
        request.setParameter("ln", "en");
        request.setParameter("foo", "bar");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", "sometree", null, "en")).thenReturn(view);

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());

        FlowSessionRecord session = new FlowSessionRecord("123a", "1234567890");
        session.setLanguage("en");
        session.set("foo", "bar");
        verify(flowSessionService).updateSession(session);
    }

    @Test
    public void shouldCreateSessionWithKookooSidForIncomingCallback() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("tree", "sometree");
        request.setParameter("ln", "en");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", "sometree", null, "en")).thenReturn(view);

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());

        verify(flowSessionService).findOrCreate("123a", "1234567890");
    }

    @Test
    public void shouldUpdateSessionWithKookooSidForOutgoingCallback() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("motech_call_id", "123a");
        request.setParameter("sid", "456b");
        request.setParameter("cid", "1234567890");
        request.setParameter("tree", "sometree");
        request.setParameter("ln", "en");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.getSession("123a")).thenReturn(flowSession);
        FlowSession newFlowSession = new FlowSessionRecord("456b", "1234567890");
        when(flowSessionService.updateSessionId("123a", "456b")).thenReturn(newFlowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("456b", "1234567890", "kookoo", "sometree", null, "en")).thenReturn(view);

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());
        verify(flowSessionService).updateSession(newFlowSession);
    }

    @Test
    public void shouldRaiseCallInitiatedEvent() {
        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", null, null, null)).thenReturn(view);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("event", "NewCall");

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());

        verify(callFlowServer).raiseCallEvent(IvrEvent.Initiated, "123a");
    }

    @Test
    public void shouldRaiseCallAnsweredEvent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("status", "answered");
        request.setParameter("status_details", "Normal");

        kookooIvrController.handleStatus(request);

        verify(callFlowServer).raiseCallEvent(IvrEvent.Answered, "123a");
    }

    @Test
    public void shouldRaiseCallUnansweredEvent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("status", "ring");
        request.setParameter("status_details", "NoAnswer");

        kookooIvrController.handleStatus(request);

        verify(callFlowServer).raiseCallEvent(IvrEvent.Unanswered, "123a");
    }

    @Test
    public void shouldRaiseHangupEvent() {
        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", null, "Hangup", null)).thenReturn(view);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("event", "Hangup");

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());

        verify(callFlowServer).raiseCallEvent(IvrEvent.Hangup, "123a");
    }

    @Test
    public void shouldRaiseDisconnectEvent() {
        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", null, "Disconnect", null)).thenReturn(view);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("event", "Disconnect");

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());

        verify(callFlowServer).raiseCallEvent(IvrEvent.Disconnected, "123a");
    }

    @Test
    public void shouldRaiseCallAnsweredEventForConnectCalls() {
        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", null, DialStatus.completed.toString(), null)).thenReturn(view);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("event", "Dial");
        request.setParameter("status", "answered");

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());

        verify(callFlowServer).raiseCallEvent(IvrEvent.DialAnswered, "123a");
    }

    @Test
    public void shouldRaiseCallUnAnsweredEventForConnectCalls() {
        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", null, DialStatus.noAnswer.toString(), null)).thenReturn(view);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("event", "Dial");
        request.setParameter("status", "not_answered");

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());

        verify(callFlowServer).raiseCallEvent(IvrEvent.DialUnanswered, "123a");
    }

    @Test
    public void shouldRaiseCallRecordEvent() {
        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(callFlowServer.getResponse("123a", "1234567890", "kookoo", null, null, null)).thenReturn(view);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("sid", "123a");
        request.setParameter("cid", "1234567890");
        request.setParameter("event", "Record");

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());

        verify(callFlowServer).raiseCallEvent(IvrEvent.DialRecord, "123a");
    }
}
