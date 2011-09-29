package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.action.UserNotFoundAction;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.ivr.kookoo.service.UserService;
import org.motechproject.server.service.ivr.*;
import org.motechproject.server.service.ivr.IVRSession.IVRCallAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class NewCallEventAction extends BaseEventAction {

    @Autowired
    private UserNotFoundAction userNotFoundAction;

    @Autowired
    private UserService userService;

    public NewCallEventAction() {
    }

    public NewCallEventAction(IVRMessage messages, UserNotFoundAction userNotFoundAction, UserService userService,
                              EventService eventService, KookooCallDetailRecordsService kookooCallDetailRecordsService) {
        this.messages = messages;
        this.userService = userService;
        this.userNotFoundAction = userNotFoundAction;
        this.kookooCallDetailRecordsService = kookooCallDetailRecordsService;
        this.eventService = eventService;
    }

    @Override
    public String createResponse(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        if (!userService.isRegisteredUser(ivrRequest.getCid())) {
            return userNotFoundAction.createResponse(ivrRequest, request, response);
        }
        IVRSession ivrSession = createIVRSession(request);
        ivrSession.set(IVRCallAttribute.CALLER_ID, ivrRequest.getCid());
        ivrSession.setState(IVRCallState.COLLECT_PIN);

        return dtmfResponseWithWav(ivrRequest, messages.getSignatureMusic());
    }
}
