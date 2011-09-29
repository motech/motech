package org.motechproject.ivr.kookoo.service;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.CallDetailRecordEvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVRRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KookooCallDetailRecordsServiceImpl implements KookooCallDetailRecordsService {

    @Autowired
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    @Autowired
    private AllKooKooCallDetailRecords allCallDetailRecords;

    @Autowired
    private EventService eventService;

    public KookooCallDetailRecord get(String callId) {
        return allKooKooCallDetailRecords.findByCallId(callId);
    }

    public String create(String vendorCallId, String callerId, IVRRequest.CallDirection callDirection) {
        CallDetailRecord callDetailRecord = null;
        if (IVRRequest.CallDirection.Inbound.equals(callDirection)) {
            callDetailRecord = CallDetailRecord.newIncomingCallRecord(callerId);
        } else {
            callDetailRecord = CallDetailRecord.newOutgoingCallRecord(callerId);
        }
        KookooCallDetailRecord record = new KookooCallDetailRecord(callDetailRecord);
        record.setVendorCallId(vendorCallId);
        allCallDetailRecords.add(record);
        return record.getId();
    }

    @Override
    public void appendEvent(String callId, String callEventKey, Map<String, String> callEventData) {
        CallEvent callEvent = new CallEvent(callEventKey, callEventData);
        KookooCallDetailRecord callDetailRecord = allKooKooCallDetailRecords.findByCallId(callId);
        callDetailRecord.addCallEvent(callEvent);
        allKooKooCallDetailRecords.update(callDetailRecord);
    }

    @Override
    public void close(String callId, String externalId) {
        get(callId).close();
        eventService.publishEvent(new CallDetailRecordEvent(callId, externalId));
    }
}
