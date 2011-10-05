package org.motechproject.ivr.kookoo.service;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.server.service.ivr.IVRRequest;

import java.util.Map;

public interface KookooCallDetailRecordsService {
    public KookooCallDetailRecord get(String callId);
    public String create(String vendorCallId, String callerId, IVRRequest.CallDirection callDirection);
    public void appendEvent(String callId, String callEventKey, Map<String, String> callEventData);
    public void close(String callId, String externalId);
}
