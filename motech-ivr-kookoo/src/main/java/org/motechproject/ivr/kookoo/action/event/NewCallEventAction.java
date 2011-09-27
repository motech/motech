package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.action.UserNotFoundAction;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.ivr.kookoo.service.UserService;
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

    private final AllKooKooCallDetailRecords allCallDetailRecords;

    @Autowired
    public NewCallEventAction(IVRMessage messages, UserNotFoundAction userNotFoundAction, UserService userService,
                              EventService eventService, IVRCallIdentifiers callIdentifiers,
                              AllKooKooCallDetailRecords allKooKooCallDetailRecords) {

        super(eventService, callIdentifiers, allKooKooCallDetailRecords);
        this.userService = userService;
        this.allCallDetailRecords = allKooKooCallDetailRecords;
        this.messages = messages;
        this.userNotFoundAction = userNotFoundAction;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        createCallDetailRecord(ivrRequest);
        if (!userService.isRegisteredUser(ivrRequest.getCid())) {
            return userNotFoundAction.handle(ivrRequest, request, response);
        }
        IVRSession ivrSession = createIVRSession(request);
        ivrSession.set(IVRCallAttribute.CALLER_ID, ivrRequest.getCid());
        ivrSession.setState(IVRCallState.COLLECT_PIN);
        return dtmfResponseWithWav(ivrRequest, messages.getSignatureMusic());
    }

    private void createCallDetailRecord(IVRRequest ivrRequest) {
        CallDetailRecord callDetailRecord = CallDetailRecord.create(ivrRequest.getSid(), ivrRequest.getCid());
        allCallDetailRecords.add(new KookooCallDetailRecord(callDetailRecord));
    }
}
