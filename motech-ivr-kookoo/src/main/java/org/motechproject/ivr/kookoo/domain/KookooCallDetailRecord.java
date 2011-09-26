package org.motechproject.ivr.kookoo.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.type === 'KookooCallDetailRecord'")
public class KookooCallDetailRecord extends MotechBaseDataObject {

    @JsonProperty("type")
    private String type = "KookooCallDetailRecord";

    private CallDetailRecord callDetailRecord;

    public KookooCallDetailRecord(){
    }

    public KookooCallDetailRecord(CallDetailRecord callDetailRecord) {
        this.callDetailRecord = callDetailRecord;
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
}
