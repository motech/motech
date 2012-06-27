package org.motechproject.server.verboice.web;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.decisiontree.FlowSession;
import org.motechproject.decisiontree.service.FlowSessionService;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.motechproject.server.verboice.domain.VerboiceHandler;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
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

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldUseHandlerWhenTreeIsNotGiven(){
        Map parameterMap = mock(Map.class);
        VerboiceHandler verboiceHandler = mock(VerboiceHandler.class);
        when(verboiceHandler.handle(parameterMap)).thenReturn("verboice response xml");
        when(verboiceIVRService.getHandler()).thenReturn(verboiceHandler);

        final HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getParameterMap()).thenReturn(parameterMap);

        assertThat(verboiceIVRController.handleRequest(mockRequest), is(equalTo("verboice response xml")));
    }

    @Test
    public void shouldupdateSessionWithVerboiceSidForOutgoingCallback() {
        VerboiceHandler handler = mock(VerboiceHandler.class);
        when(verboiceIVRService.getHandler()).thenReturn(handler);
        FlowSession flowSession = mock(FlowSession.class);
        when(flowSession.getSessionId()).thenReturn("foo");
        when(flowSessionService.getSession("foo")).thenReturn(flowSession);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("motech_call_id", "foo");
        request.setParameter("CallSid", "bar");
        verboiceIVRController.handleRequest(request);
        verify(flowSessionService).updateSessionId("foo", "bar");
        verify(flowSessionService).getSession("foo");
    }

}
