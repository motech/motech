package org.motechproject.ivr.kookoo.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.util.DateUtil;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;

public class KookooCallDetailRecordTest {
    @Test
    public void appendToLastEvent() {
        DateTime now = DateUtil.now();
        CallDetailRecord callDetailRecord = new CallDetailRecord(now.minusMinutes(2).toDate(), now.toDate(), now.minusMinutes(1).toDate(), CallDetailRecord.Disposition.ANSWERED, 60);
        KookooCallDetailRecord kookooCallDetailRecord = new KookooCallDetailRecord(callDetailRecord);
        HashMap<String, String> data = new HashMap<String, String>();
        CallEvent callEvent = new CallEvent("NewCall", data);
        kookooCallDetailRecord.addCallEvent(callEvent);
        data.put("foo", "bar");
        kookooCallDetailRecord.appendToLastEvent(data);
        assertEquals(1, data.size());
    }
}
