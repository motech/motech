package org.motechproject.ivr.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.action.UserNotFoundAction;
import org.motechproject.ivr.service.UserService;
import org.motechproject.server.service.ivr.*;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class NewCallEventAction extends BaseEventAction {


    private UserNotFoundAction userNotFoundAction;
    private UserService userService;

    @Autowired
    public NewCallEventAction(IVRMessage messages, UserNotFoundAction userNotFoundAction, EventService eventService, UserService userService) {
        this.userService = userService;
        this.messages = messages;
        this.userNotFoundAction = userNotFoundAction;
        this.eventService = eventService;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        if (!userService.isRegisteredUser(ivrRequest.getCallerId())) {
            return userNotFoundAction.handle(ivrRequest, request, response);
        }

        IVRSession ivrSession = createIVRSession(request);
        ivrSession.set(IVRCallAttribute.CALLER_ID, ivrRequest.getCallerId());
        ivrSession.setState(IVRCallState.COLLECT_PIN);
        return dtmfResponseWithWav(ivrRequest, messages.getSignatureMusic());
    }
}
