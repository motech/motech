package org.motechproject.ivr.kookoo.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooCallServiceImpl;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.action.Actions;
import org.motechproject.ivr.kookoo.action.event.BaseEventAction;
import org.motechproject.server.service.ivr.IVREvent;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class IVRControllerTest {
    IVRController controller;
    @Mock
    private KookooRequest ivrRequest;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Actions actions;
    @Mock
    private BaseEventAction action;
    @Mock
    private KookooCallServiceImpl kookooCallService;

    @Before
    public void setUp() {
        initMocks(this);
        controller = new IVRController(actions, kookooCallService);
        when(ivrRequest.callEvent()).thenReturn(IVREvent.HANGUP);
        when(request.getCookies()).thenReturn(new Cookie[0]);
        when(actions.findFor(IVREvent.HANGUP)).thenReturn(action);
         when(ivrRequest.getParameter("CallId")).thenReturn(null);
    }

    @Test
    public void shouldDelegateToActionsToHandleTheRequest() {
        when(kookooCallService.generateCallId(ivrRequest)).thenReturn("callId");
        when(action.handle("callId", ivrRequest, request, response)).thenReturn("reply");

        String reply = controller.reply(ivrRequest, request, response);

        verify(action).handle("callId", ivrRequest, request, response);
        assertEquals("reply", reply);
    }

    @Test
    public void shouldGenerateCallId_ForNewCall() {
        controller.reply(ivrRequest, request, response);
        verify(kookooCallService).generateCallId(ivrRequest);
    }

    @Test
    public void shouldNotGenerateNewCallId_WhenCallIdIsPresentInRequestAsACookie() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("CallId","callId")});

        controller.reply(ivrRequest, request, response);
        verify(kookooCallService, never()).generateCallId(ivrRequest);
    }

    @Test
    public void shouldNotGenerateNewCallId_WhenCallIdIsPresentInRequestAsParameter() {
        when(ivrRequest.getParameter("CallId")).thenReturn("callId");

        controller.reply(ivrRequest, request, response);
        verify(kookooCallService, never()).generateCallId(ivrRequest);
    }

    @Test
    public void shouldSetCallIdInCookie_WhenCallIdNotPresentInRequestAsACookie() {
        when(kookooCallService.generateCallId(ivrRequest)).thenReturn("callId");
        controller.reply(ivrRequest, request, response);

        ArgumentCaptor<Cookie> cookieCapture = ArgumentCaptor.forClass(Cookie.class);
        verify(response).addCookie(cookieCapture.capture());
        assertEquals("CallId", cookieCapture.getValue().getName());
        assertEquals("callId", cookieCapture.getValue().getValue());
    }
}
