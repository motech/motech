package org.motechproject.ivr.kookoo.service;

import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.event.IVREvent;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.ivr.model.CallDirection;

import java.util.Map;

/**
 *   Kookoo Call Detail Record Service provides call log extension point.
 *   Supports ivr events such as call answered, Record etc ({@link org.motechproject.ivr.event.IVREvent IVREvent})
 */
public interface KookooCallDetailRecordsService {
    /**
     * Access call detail record for current call.
     * @param callDetailRecordId
     * @return KookooCallDetailRecord
     */
    public KookooCallDetailRecord get(String callDetailRecordId);

    /**
     * Adds an answered call details to call log.
     * @param vendorCallId
     * @param callerId
     * @param callDirection
     * @return callDetailRecordId
     */
    public String createAnsweredRecord(String vendorCallId, String callerId, CallDirection callDirection);

    /**
     * Adds an outgoing call details to call log.
     * @param callerId
     * @param disposition
     * @return callDetail
     */
    public String createOutgoing(String callerId, CallDetailRecord.Disposition disposition);

    /**
     * Adds given call event to current call detail record.
     * @param callDetailRecordId: current call detail record
     * @param callEvent: IVR event such as Newcall, Record, Dial
     * @param userInput: DTMF digit pressed.
     * @see org.motechproject.ivr.event.IVREvent
     */
    public void appendEvent(String callDetailRecordId, IVREvent callEvent, String userInput);

    /**
     * Marks the end of call either on Hangup event or Disconnect event.
     * @param callDetailRecordId: current call detail record
     * @param externalId: unique Id representing caller.
     * @param callEvent: IVR event such as Newcall, Record, Dial
     */
    public void close(String callDetailRecordId, String externalId, CallEvent callEvent);

    /**
     * Adds additional data to call log.
     * @param callDetailRecordID: current call detail record
     * @param map: Map of String key value pairs.
     */
    void appendToLastCallEvent(String callDetailRecordID,  Map<String, String> map);

    /**
     * Update call record as answered.
     * @param vendorCallId: vendor session id.
     * @param callDetailRecordID: current call detail record
     */
    void setCallRecordAsAnswered(String vendorCallId, String callDetailRecordID);

    /**
     * Update call record as not answered.
     * @param callDetailRecordID: current call detail record
     */
    void setCallRecordAsNotAnswered(String callDetailRecordID);
}
