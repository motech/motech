package org.motechproject.ivr.kookoo.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.ivr.event.CallEvent;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.util.DateUtil;

/**
 * Call Detail Record represents call events and data captured in a call, stored as call log couch db document
 * Also contains call meta information like call direction, duration etc.
 */
@TypeDiscriminator("doc.type === 'KookooCallDetailRecord'")
public class KookooCallDetailRecord extends MotechBaseDataObject {

    private String vendorCallId;

    private CallDetailRecord callDetailRecord;

    private KookooCallDetailRecord() { }

    /**
     * Construct KookooCallDetailRecord given call detail record and vendor side call id.
     * @param callDetailRecord
     * @param vendorCallId
     */
    public KookooCallDetailRecord(CallDetailRecord callDetailRecord, String vendorCallId) {
        this.callDetailRecord = callDetailRecord;
        this.vendorCallId = vendorCallId;
    }

    public CallDetailRecord getCallDetailRecord() {
        return callDetailRecord;
    }

    public void setCallDetailRecord(CallDetailRecord callDetailRecord) {
        this.callDetailRecord = callDetailRecord;
    }

    /**
     * Set end time for call, usually called on hangup event or disconnect event.
     */
    public void close() {
        callDetailRecord.setEndDate(DateUtil.now().toDate());
    }

    /**
     * Adds call event to call detail record.
     * @param callEvent
     */
    public void addCallEvent(CallEvent callEvent) {
        callDetailRecord.addCallEvent(callEvent);
    }

    public String getVendorCallId() {
        return vendorCallId;
    }

    public void setVendorCallId(String vendorCallId) {
        this.vendorCallId = vendorCallId;
    }

    /**
     * Add additional key value data to last call event.
     * @param key
     * @param value
     */
    public void appendToLastEvent(String key, String value) {
        CallEvent callEvent = callDetailRecord.lastCallEvent();
        callEvent.appendData(key, value);
    }
}
