package org.motechproject.ivr.kookoo.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.util.DateUtil;

import java.util.Map;

@TypeDiscriminator("doc.type === 'KookooCallDetailRecord'")
public class KookooCallDetailRecord extends MotechBaseDataObject {
    @JsonProperty("type")
    private String type = "KookooCallDetailRecord";

    private String vendorCallId;

    private CallDetailRecord callDetailRecord;

    private KookooCallDetailRecord(){
    }

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

    public void close() {
        callDetailRecord.setEndDate(DateUtil.now().toDate());
    }

    public void addCallEvent(CallEvent callEvent) {
        callDetailRecord.addCallEvent(callEvent);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVendorCallId() {
        return vendorCallId;
    }

    public void setVendorCallId(String vendorCallId) {
        this.vendorCallId = vendorCallId;
    }

    public void appendToLastEvent(String key, String value) {
        CallEvent callEvent = callDetailRecord.lastCallEvent();
        callEvent.appendData(key, value);
    }
}
