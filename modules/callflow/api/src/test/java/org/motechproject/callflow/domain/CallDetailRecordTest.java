package org.motechproject.callflow.domain;

import org.junit.Test;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.ivr.domain.CallEvent;

import static junit.framework.Assert.assertEquals;

public class CallDetailRecordTest {
    @Test
    public void lastCallEvent() {
        CallDetailRecord callDetailRecord = CallDetailRecord.create("43435", CallDirection.Inbound, CallDisposition.ANSWERED);
        assertEquals(null, callDetailRecord.lastCallEvent());
        String eventName = "Foo";
        callDetailRecord.addCallEvent(new CallEvent(eventName));
        assertEquals(eventName, callDetailRecord.lastCallEvent().getName());
    }
}
