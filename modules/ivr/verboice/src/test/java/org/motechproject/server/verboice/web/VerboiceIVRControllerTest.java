package org.motechproject.server.verboice.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.callflow.domain.FlowSessionRecord;
import org.motechproject.callflow.service.CallFlowServer;
import org.motechproject.callflow.service.FlowSessionService;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class VerboiceIVRControllerTest {

    @InjectMocks
    private VerboiceIVRController verboiceIvrController = new VerboiceIVRController();

    @Mock
    private VerboiceIVRService verboiceIVRService;
    @Mock
    private FlowSessionService flowSessionService;
    @Mock
    private CallFlowServer decisionTreeServer;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldRenderVerboiceNodeRespone() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("CallSid", "123a");
        request.setParameter("From", "1234567890");
        request.setParameter("tree", "sometree");
        request.setParameter("ln", "en");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(decisionTreeServer.getResponse("123a", "1234567890", "verboice", "sometree", null, "en")).thenReturn(view);

        assertEquals(view, verboiceIvrController.handle(request, new MockHttpServletResponse()));
    }

    @Test
    public void shouldPopulateSessionFields() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("CallSid", "123a");
        request.setParameter("From", "1234567890");
        request.setParameter("tree", "sometree");
        request.setParameter("ln", "en");
        request.setParameter("foo", "bar");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(decisionTreeServer.getResponse("123a", "1234567890", "verboice", "sometree", null, "en")).thenReturn(view);

        verboiceIvrController.handle(request, new MockHttpServletResponse());

        FlowSessionRecord session = new FlowSessionRecord("123a", "1234567890");
        session.setLanguage("en");
        session.set("foo", "bar");
        verify(flowSessionService).updateSession(session);
    }

    @Test
    public void shouldCreateSessionWithVerboiceSidForIncomingCallback() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("CallSid", "123a");
        request.setParameter("From", "1234567890");
        request.setParameter("tree", "sometree");
        request.setParameter("ln", "en");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.findOrCreate("123a", "1234567890")).thenReturn(flowSession);

        ModelAndView view = new ModelAndView();
        when(decisionTreeServer.getResponse("123a", "1234567890", "verboice", "sometree", null, "en")).thenReturn(view);

        verboiceIvrController.handle(request, new MockHttpServletResponse());

        verify(flowSessionService).findOrCreate("123a", "1234567890");
    }

    @Test
    public void shouldupdateSessionWithVerboiceSidForOutgoingCallback() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("motech_call_id", "123a");
        request.setParameter("CallSid", "456b");
        request.setParameter("From", "1234567890");
        request.setParameter("tree", "sometree");
        request.setParameter("ln", "en");

        FlowSession flowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.getSession("123a")).thenReturn(flowSession);
        FlowSession newFlowSession = new FlowSessionRecord("123a", "1234567890");
        when(flowSessionService.updateSessionId("123a", "456b")).thenReturn(newFlowSession);

        ModelAndView view = new ModelAndView();
        when(decisionTreeServer.getResponse("456b", "1234567890", "verboice", "sometree", null, "en")).thenReturn(view);

        verboiceIvrController.handle(request, new MockHttpServletResponse());

        verify(flowSessionService).updateSessionId("123a", "456b");
    }
}
