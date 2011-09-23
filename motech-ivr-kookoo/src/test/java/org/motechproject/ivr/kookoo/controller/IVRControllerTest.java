package org.motechproject.ivr.kookoo.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.action.Actions;
import org.motechproject.ivr.kookoo.action.event.BaseEventAction;
import org.motechproject.server.service.ivr.IVREvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Before
    public void setUp() {
        initMocks(this);
        controller = new IVRController(actions);
    }

    @Test
    public void shouldDelegateToActionsToHandleTheRequest() {
        when(ivrRequest.callEvent()).thenReturn(IVREvent.HANGUP);
        when(actions.findFor(IVREvent.HANGUP)).thenReturn(action);
        when(action.handleInternal(ivrRequest, request, response)).thenReturn("reply");

        String reply = controller.reply(ivrRequest, request, response);

        verify(action).handleInternal(ivrRequest, request, response);
        assertEquals("reply", reply);
    }
}
