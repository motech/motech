package org.motechproject.ivr.kookoo.service;

import org.apache.commons.lang.StringUtils;
import org.motechproject.context.EventContext;
import org.motechproject.event.EventRelay;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.eventlogging.CallEventConstants;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;
import org.motechproject.model.MotechEvent;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class KookooCallDetailRecordsServiceImpl implements KookooCallDetailRecordsService {

    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;
    private AllKooKooCallDetailRecords allCallDetailRecords;
    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

    public static final String CLOSE_CALL_SUBJECT = "close_call";
    public static final String CALL_ID = "call_id";
    public static final String EXTERNAL_ID = "external_id";

    @Autowired
    public KookooCallDetailRecordsServiceImpl(AllKooKooCallDetailRecords allKooKooCallDetailRecords, AllKooKooCallDetailRecords allCallDetailRecords) {
        this.allKooKooCallDetailRecords = allKooKooCallDetailRecords;
        this.allCallDetailRecords = allCallDetailRecords;
    }

    public KookooCallDetailRecord get(String callDetailRecordId) {
        return allKooKooCallDetailRecords.get(callDetailRecordId);
    }

    public String createAnsweredRecord(String vendorCallId, String callerId, CallDirection callDirection) {
        CallDetailRecord callDetailRecord = CallDetailRecord.create(callerId, callDirection, CallDetailRecord.Disposition.ANSWERED);
        return addCallDetailRecord(vendorCallId, callDetailRecord);
    }

    public String createOutgoing(String callerId, CallDetailRecord.Disposition disposition) {
        CallDetailRecord callDetailRecord = CallDetailRecord.create(callerId, CallDirection.Outbound, disposition);
        return addCallDetailRecord(null, callDetailRecord);
    }

    private String addCallDetailRecord(String vendorCallId, CallDetailRecord callDetailRecord) {
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord, vendorCallId);
        allCallDetailRecords.add(kookooCallDetailRecord);
        return kookooCallDetailRecord.getId();
    }

    @Override
    public void appendEvent(String callDetailRecordId, IVREvent callEvent, String userInput) {
        KookooCallDetailRecord kookooCallDetailRecord = appendToCallDetailRecord(callDetailRecordId, new CallEvent(callEvent.toString()));
        if (!StringUtils.isEmpty(userInput)) {
            kookooCallDetailRecord.appendToLastEvent(CallEventConstants.DTMF_DATA, userInput);
        }
        allKooKooCallDetailRecords.update(kookooCallDetailRecord);
    }

    private KookooCallDetailRecord appendToCallDetailRecord(String callDetailRecordId, CallEvent callEvent) {
        KookooCallDetailRecord callDetailRecord = get(callDetailRecordId);
        callDetailRecord.addCallEvent(callEvent);
        return callDetailRecord;
    }

    @Override
    public void appendToLastCallEvent(String callDetailRecordID, HashMap<String, String> map) {
        KookooCallDetailRecord callDetailRecord = get(callDetailRecordID);
        for (String key : map.keySet()) {
            callDetailRecord.appendToLastEvent(key, map.get(key));
        }
        allKooKooCallDetailRecords.update(callDetailRecord);
    }

    @Override
    public void setCallRecordAsAnswered(String vendorCallId, String callDetailRecordID) {
        KookooCallDetailRecord callDetailRecord = get(callDetailRecordID);
        callDetailRecord.setVendorCallId(vendorCallId);
        CallDetailRecord record = callDetailRecord.getCallDetailRecord();
        record.setDisposition(CallDetailRecord.Disposition.ANSWERED);
        record.setAnswerDate(DateUtil.now().toDate());
        allKooKooCallDetailRecords.update(callDetailRecord);

    }

    @Override
    public void setCallRecordAsNotAnswered(String callDetailRecordID) {
        KookooCallDetailRecord callDetailRecord = get(callDetailRecordID);
        CallDetailRecord record = callDetailRecord.getCallDetailRecord();
        record.setDisposition(CallDetailRecord.Disposition.NO_ANSWER);
        allKooKooCallDetailRecords.update(callDetailRecord);
    }

    @Override
    public void close(String callDetailRecordId, String externalId, CallEvent callEvent) {
        KookooCallDetailRecord kookooCallDetailRecord = appendToCallDetailRecord(callDetailRecordId, callEvent);
        kookooCallDetailRecord.close();
        allKooKooCallDetailRecords.update(kookooCallDetailRecord);
        raiseCloseCallEvent(callDetailRecordId, externalId);
    }

    private void raiseCloseCallEvent(String callDetailRecordId, String externalId) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put(CALL_ID, callDetailRecordId);
        data.put(EXTERNAL_ID, externalId);
        eventRelay.sendEventMessage(new MotechEvent(CLOSE_CALL_SUBJECT, data));
    }
}
