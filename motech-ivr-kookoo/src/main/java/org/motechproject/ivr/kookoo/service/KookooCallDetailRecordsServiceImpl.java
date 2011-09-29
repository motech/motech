package org.motechproject.ivr.kookoo.service;

import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KookooCallDetailRecordsServiceImpl implements KookooCallDetailRecordsService {

    @Autowired
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    @Autowired
    private AllKooKooCallDetailRecords allCallDetailRecords;

    public KookooCallDetailRecord findByCallId(String callId) {
        return allKooKooCallDetailRecords.findByCallId(callId);
    }

    public String create(CallDetailRecord callDetailRecord) {
        KookooCallDetailRecord record = new KookooCallDetailRecord(callDetailRecord);
        allCallDetailRecords.add(record);
        return record.getCallId();
    }

    @Override
    public void appendEvent(String callId, CallEvent callEvent) {
        KookooCallDetailRecord callDetailRecord = allKooKooCallDetailRecords.findByCallId(callId);
        callDetailRecord.addCallEvent(callEvent);
        allKooKooCallDetailRecords.update(callDetailRecord);
    }
}
