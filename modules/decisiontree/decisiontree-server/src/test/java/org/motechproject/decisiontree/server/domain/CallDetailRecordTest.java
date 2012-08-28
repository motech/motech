package org.motechproject.decisiontree.server.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CallDetailRecordTest {
    @Test
    public void lastCallEvent() {
        CallDetailRecord callDetailRecord = CallDetailRecord.create("43435", CallDirection.Inbound, CallDetailRecord.Disposition.ANSWERED);
        assertEquals(null, callDetailRecord.lastCallEvent());
        String eventName = "Foo";
        callDetailRecord.addCallEvent(new CallEvent(eventName));
        assertEquals(eventName, callDetailRecord.lastCallEvent().getName());
    }
}
