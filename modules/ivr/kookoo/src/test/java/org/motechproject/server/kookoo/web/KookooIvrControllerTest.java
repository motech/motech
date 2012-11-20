package org.motechproject.server.kookoo.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.domain.FlowSessionRecord;
import org.motechproject.decisiontree.server.service.DecisionTreeServer;
import org.motechproject.decisiontree.server.service.FlowSessionService;
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
    private DecisionTreeServer decisionTreeServer;
    @Mock
    private FlowSessionService flowSessionService;

    @Before
    public void setup() {
        initMocks(this);
        kookooIvrController = new KookooIvrController(decisionTreeServer, flowSessionService);
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
        when(decisionTreeServer.getResponse("123a", "1234567890", "kookoo", "sometree", null, "en")).thenReturn(view);

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
        when(decisionTreeServer.getResponse("123a", "1234567890", "kookoo", "sometree", null, "en")).thenReturn(view);

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
        when(decisionTreeServer.getResponse("123a", "1234567890", "kookoo", "sometree", null, "en")).thenReturn(view);

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
        when(decisionTreeServer.getResponse("456b", "1234567890", "kookoo", "sometree", null, "en")).thenReturn(view);

        kookooIvrController.ivrCallback(request, new MockHttpServletResponse());
        verify(flowSessionService).updateSession(newFlowSession);
    }
}
