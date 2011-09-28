package org.motechproject.ivr.kookoo.action.event;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.EndOfCallEvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class HangupEventAction extends BaseEventAction {

    @Autowired
    public HangupEventAction(EventService eventService, AllKooKooCallDetailRecords allKooKooCallDetailRecords) {
        this.eventService = eventService;
        this.allKooKooCallDetailRecords = allKooKooCallDetailRecords;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    @Override
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String callId = ivrRequest.getSid();
        KookooCallDetailRecord callDetailRecord = allKooKooCallDetailRecords.findByCallId(callId);
        callDetailRecord.close();
        raiseDisconnectEvent(getIVRSession(request), callDetailRecord);
        getIVRSession(request).close();
        return;
    }

    private void raiseDisconnectEvent(IVRSession ivrSession, KookooCallDetailRecord kookooCallDetailRecord) {
        String callId = kookooCallDetailRecord.getCallDetailRecord().getCallId();
        eventService.publishEvent(new EndOfCallEvent(callId, ivrSession.getExternalId()));
    }
}
