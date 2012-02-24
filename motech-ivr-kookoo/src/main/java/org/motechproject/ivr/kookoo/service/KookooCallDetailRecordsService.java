package org.motechproject.ivr.kookoo.service;

import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;

import java.util.HashMap;

public interface KookooCallDetailRecordsService {
    public KookooCallDetailRecord get(String callDetailRecordId);
    public String createAnsweredRecord(String vendorCallId, String callerId, CallDirection callDirection);
    public String createOutgoing(String vendorCallId, String callerId, CallDetailRecord.Disposition disposition);
    public void appendEvent(String callDetailRecordId, IVREvent callEvent, String userInput);
    public void close(String callDetailRecordId, String externalId, IVREvent event);
    void appendToLastCallEvent(String callDetailRecordID,  HashMap<String, String> map);
}
