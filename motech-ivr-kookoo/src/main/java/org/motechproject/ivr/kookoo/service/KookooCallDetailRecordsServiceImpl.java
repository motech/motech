package org.motechproject.ivr.kookoo.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.eventtracking.service.EventService;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.CallDetailRecordEvent;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        CallDetailRecord callDetailRecord = CallDirection.Inbound.equals(callDirection) ? CallDetailRecord.newIncomingCallRecord(callerId) :
                CallDetailRecord.newOutgoingCallRecord(callerId);
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord, vendorCallId);
        kookooCallDetailRecord.addCallEvent(new CallEvent(IVREvent.NewCall.toString()));
        allCallDetailRecords.add(kookooCallDetailRecord);
        return kookooCallDetailRecord.getId();
    }

    @Override
    public void appendEvent(String callDetailRecordId, IVREvent callEvent, String userInput) {
        if (IVREvent.GotDTMF.equals(callEvent) && StringUtils.isEmpty(userInput)) return;

        KookooCallDetailRecord kookooCallDetailRecord = appendToCallDetailRecord(callDetailRecordId, callEvent);
        if (!StringUtils.isEmpty(userInput)) {
            kookooCallDetailRecord.appendToLastEvent(CallEventConstants.DTMF_DATA, userInput);
        }
        allKooKooCallDetailRecords.update(kookooCallDetailRecord);
    }

    private KookooCallDetailRecord appendToCallDetailRecord(String callDetailRecordId, IVREvent ivrEvent) {
        KookooCallDetailRecord callDetailRecord = get(callDetailRecordId);
        CallEvent callEvent = new CallEvent(ivrEvent.toString());
        callDetailRecord.addCallEvent(callEvent);
        return callDetailRecord;
    }

    @Override
    public void appendToLastCallEvent(String callDetailRecordID, KookooIVRResponseBuilder ivrResponseBuilder, String response) {
        KookooCallDetailRecord callDetailRecord = get(callDetailRecordID);
        if (ivrResponseBuilder.isEmpty()) return;
        callDetailRecord.appendToLastEvent(CallEventConstants.CUSTOM_DATA_LIST, response);
        allKooKooCallDetailRecords.update(callDetailRecord);
    }

    @Override
    public void close(String callDetailRecordId, String externalId, IVREvent event) {
        KookooCallDetailRecord kookooCallDetailRecord = appendToCallDetailRecord(callDetailRecordId, event);
        kookooCallDetailRecord.close();
        allKooKooCallDetailRecords.update(kookooCallDetailRecord);
        eventService.publishEvent(new CallDetailRecordEvent(callDetailRecordId, externalId));
    }
}
