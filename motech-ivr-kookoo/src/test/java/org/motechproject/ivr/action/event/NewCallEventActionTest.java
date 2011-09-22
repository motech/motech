package org.motechproject.ivr.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.motechproject.eventtracking.domain.Event;
import org.motechproject.ivr.IVRCallEvent;
import org.motechproject.ivr.action.UserNotFoundAction;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.service.UserService;
import org.motechproject.server.service.ivr.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;


public class NewCallEventActionTest extends BaseActionTest {
    private NewCallEventAction action;
    @Mock
    private UserService userService;
    @Mock
    private IVRMessage ivrMessages;
    @Mock
    private UserNotFoundAction userNotFoundAction;

    @Before
    public void setUp() {
        initMocks(this);
        action = new NewCallEventAction(ivrMessages, userNotFoundAction, userService, eventService, callIdentifiers);
    }

    @Test
    public void shouldAskUserNotFoundActionToHandleIfUserIsNotRegistered() {
        IVRRequest ivrRequest = new KookooRequest();
        when(userService.isRegisteredUser(ivrRequest.getCallerId())).thenReturn(false);

        action.handle(ivrRequest, request, response);
        verify(userNotFoundAction).handle(ivrRequest, request, response);
    }

    @Test
    public void shouldSetAttributesInSessionAndSendDtmfResponseWithWav() {
        IVRRequest ivrRequest = new KookooRequest();
        when(userService.isRegisteredUser(ivrRequest.getCallerId())).thenReturn(true);
        when(request.getSession()).thenReturn(session);

        String xmlResponse = action.handle(ivrRequest, request, response);
        verify(session).setAttribute(IVRSession.IVRCallAttribute.CALLER_ID, ivrRequest.getCallerId());
        verify(session).setAttribute(IVRSession.IVRCallAttribute.CALL_STATE, IVRCallState.COLLECT_PIN);
        assertEquals("<response><collectdtmf><playaudio/></collectdtmf></response>", sanitize(xmlResponse));
    }

    @Test
    public void shouldTestPublishEventForAOnGoingCallWhereCallIdIsPickedFromSession() {
        IVRRequest ivrRequest = new KookooRequest("sid", "cid", "newcall", "date");
        when(userService.isRegisteredUser(ivrRequest.getCallerId())).thenReturn(true);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRSession.IVRCallAttribute.EXTERNAL_ID)).thenReturn("external_id");
        when(session.getAttribute(IVRSession.IVRCallAttribute.CALL_ID)).thenReturn("call_id");
        when(request.getParameterMap()).thenReturn(new HashMap());

        action.handleInternal(ivrRequest, request, response);

        verify(eventService).publishEvent(argThat(new EventMatcherForOldCall()));
    }

    @Test
    public void shouldTestPublishEventForANewCallWhereNewCallIdIsSetInSession() {
        IVRRequest ivrRequest = new KookooRequest("sid", "cid", "newcall", "date");
        when(userService.isRegisteredUser(ivrRequest.getCallerId())).thenReturn(true);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute(IVRSession.IVRCallAttribute.EXTERNAL_ID)).thenReturn("external_id");
        when(request.getParameterMap()).thenReturn(new HashMap());
        when(callIdentifiers.getNew()).thenReturn("call_id");
        action.handleInternal(ivrRequest, request, response);

        verify(eventService).publishEvent(argThat(new EventMatcherForNewCall()));
        verify(callIdentifiers).getNew();
    }


    public class EventMatcherForOldCall extends ArgumentMatcher<Event> {
        @Override
        public boolean matches(Object o) {
            IVRCallEvent event = (IVRCallEvent) o;
            System.out.println(event.getCallId());
            return "external_id".equals(event.getExternalID())
                    && "call_id".equals(event.getCallId());
        }
    }

    public class EventMatcherForNewCall extends ArgumentMatcher<Event> {
        @Override
        public boolean matches(Object o) {
            IVRCallEvent event = (IVRCallEvent) o;
            System.out.println(event.getCallId());
            return "external_id".equals(event.getExternalID())
                    && event.getCallId() == null;
        }
    }


}
