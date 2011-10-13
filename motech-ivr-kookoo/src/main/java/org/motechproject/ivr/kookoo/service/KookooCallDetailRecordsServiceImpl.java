package org.motechproject.ivr.kookoo.service;

import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.kookoo.CallDetailRecordEvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.server.service.ivr.CallEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KookooCallDetailRecordsServiceImpl implements KookooCallDetailRecordsService {
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;
    private EventService eventService;
    private AllKooKooCallDetailRecords allCallDetailRecords;

    @Autowired
    public KookooCallDetailRecordsServiceImpl(AllKooKooCallDetailRecords allKooKooCallDetailRecords, EventService eventService, AllKooKooCallDetailRecords allCallDetailRecords) {
        this.allKooKooCallDetailRecords = allKooKooCallDetailRecords;
        this.eventService = eventService;
        this.allCallDetailRecords = allCallDetailRecords;
    }

    public KookooCallDetailRecord get(String callDetailRecordId) {
        return allKooKooCallDetailRecords.get(callDetailRecordId);
    }

    public String create(String vendorCallId, String callerId, CallDirection callDirection) {
        CallDetailRecord callDetailRecord;
        if (CallDirection.Inbound.equals(callDirection)) {
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
    public void appendEvent(String callDetailRecordId, String callEventKey) {
        KookooCallDetailRecord callDetailRecord = appendToCallDetailRecord(callDetailRecordId, callEventKey);
        allKooKooCallDetailRecords.update(callDetailRecord);
    }

    private KookooCallDetailRecord appendToCallDetailRecord(String callDetailRecordId, String callEventKey) {
        KookooCallDetailRecord callDetailRecord = get(callDetailRecordId);
        CallEvent callEvent = new CallEvent(callEventKey, new HashMap<String, String>());
        callDetailRecord.addCallEvent(callEvent);
        return callDetailRecord;
    }

    @Override
    public void appendLastEventResponse(String callDetailRecordId, Map<String, String> map) {
        KookooCallDetailRecord callDetailRecord = get(callDetailRecordId);
        callDetailRecord.appendToLastEvent(map);
        allKooKooCallDetailRecords.update(callDetailRecord);
    }

    @Override
    public void close(String callDetailRecordId, String externalId, String event) {
        KookooCallDetailRecord kookooCallDetailRecord = appendToCallDetailRecord(callDetailRecordId, event);
        kookooCallDetailRecord.close();
        allKooKooCallDetailRecords.update(kookooCallDetailRecord);
        eventService.publishEvent(new CallDetailRecordEvent(callDetailRecordId, externalId));
    }
}
