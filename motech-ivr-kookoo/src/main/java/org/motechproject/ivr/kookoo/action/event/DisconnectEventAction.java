package org.motechproject.ivr.kookoo.action.event;

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
public class DisconnectEventAction extends BaseEventAction {

    private final AllKooKooCallDetailRecords allCallDetailRecords;

    @Autowired
    public DisconnectEventAction(AllKooKooCallDetailRecords allCallDetailRecords) {
        this.allCallDetailRecords = allCallDetailRecords;
    }

    @Override
    public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
        String callerId = ivrRequest.getCallerId();
        KookooCallDetailRecord callDetailRecord = allCallDetailRecords.findByCallId(callerId);
        callDetailRecord.callEnded();
        raiseDisconnectEvent(getIVRSession(request), callDetailRecord);
        return null;
    }

    @Override
    public void postHandle(IVRRequest ivrRequest, HttpServletRequest request,
                           HttpServletResponse response) {
        getIVRSession(request).close();
    }

    private void raiseDisconnectEvent(IVRSession ivrSession, KookooCallDetailRecord kookooCallDetailRecord) {
        String referenceId = (String) ivrSession.get("reference-id");
        String callId = kookooCallDetailRecord.getCallDetailRecord().getCallId();
        eventService.publishEvent(new EndOfCallEvent(callId, referenceId));
    }
}
