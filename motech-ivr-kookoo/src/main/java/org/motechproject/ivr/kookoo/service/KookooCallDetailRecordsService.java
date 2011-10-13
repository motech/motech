package org.motechproject.ivr.kookoo.service;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.server.service.ivr.CallDirection;

import java.util.Map;

public interface KookooCallDetailRecordsService {
    public KookooCallDetailRecord get(String callDetailRecordId);
    public String create(String vendorCallId, String callerId, CallDirection callDirection);
    public void appendEvent(String callDetailRecordId, String callEventKey);
    public void appendLastEventResponse(String callDetailRecordId, Map<String, String> map);
    public void close(String callDetailRecordId, String externalId, String event);
}
