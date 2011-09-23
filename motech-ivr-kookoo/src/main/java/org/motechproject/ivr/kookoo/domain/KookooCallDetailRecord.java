package org.motechproject.ivr.kookoo.domain;

import org.motechproject.model.MotechBaseDataObject;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.util.DateUtil;

public class KookooCallDetailRecord extends MotechBaseDataObject {

    private CallDetailRecord callDetailRecord;

    public KookooCallDetailRecord(CallDetailRecord callDetailRecord) {
        this.callDetailRecord = callDetailRecord;
    }

    public CallDetailRecord getCallDetailRecord() {
        return callDetailRecord;
    }

    public void setCallDetailRecord(CallDetailRecord callDetailRecord) {
        this.callDetailRecord = callDetailRecord;
    }

    public void callEnded() {
        callDetailRecord.setEndDate(DateUtil.now().toDate());
    }

    public void addCallEvent(CallEvent callEvent) {
        callDetailRecord.addCallEvent(callEvent);
    }
}
