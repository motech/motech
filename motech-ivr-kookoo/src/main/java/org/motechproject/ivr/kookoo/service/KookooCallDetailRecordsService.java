package org.motechproject.ivr.kookoo.service;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallEvent;

public interface KookooCallDetailRecordsService {
    public KookooCallDetailRecord findByCallId(String callId);
    public String create(CallDetailRecord callDetailRecord);
    public void appendEvent(String callId, CallEvent callEvent);
}
