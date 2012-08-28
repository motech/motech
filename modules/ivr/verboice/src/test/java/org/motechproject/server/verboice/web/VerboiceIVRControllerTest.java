package org.motechproject.server.verboice.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.server.domain.FlowSessionRecord;
import org.motechproject.decisiontree.server.service.DecisionTreeServer;
import org.motechproject.decisiontree.server.service.FlowSessionService;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.motechproject.server.verboice.domain.VerboiceHandler;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class VerboiceIVRControllerTest {

    @InjectMocks
    private VerboiceIVRController verboiceIVRController = new VerboiceIVRController();

    @Mock
    private VerboiceIVRService verboiceIVRService;
    @Mock
    private FlowSessionService flowSessionService;
    @Mock
    private DecisionTreeServer decisionTreeServer;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldupdateSessionWithVerboiceSidForOutgoingCallback() {
        FlowSession flowSession = new FlowSessionRecord("foo", "1234567890");
        when(flowSessionService.getSession("foo")).thenReturn(flowSession);

        when(decisionTreeServer.getResponse(anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(new ModelAndView());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("motech_call_id", "foo");
        request.setParameter("CallSid", "bar");

        verboiceIVRController.handle(request, new MockHttpServletResponse());

        verify(flowSessionService).updateSessionId("foo", "bar");
        verify(flowSessionService).getSession("foo");
    }
}
