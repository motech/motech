package org.motechproject.ivr.kookoo.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.repository.AllKooKooCallDetailRecords;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.CallEvent;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class BaseEventActionTest extends BaseActionTest {

    private TestEventAction testEventAction;

    @Mock
    private AllKooKooCallDetailRecords allKooKooCallDetailRecords;

    @Before
    public void setUp(){
        super.setUp();
        testEventAction = new TestEventAction(allKooKooCallDetailRecords);
    }

    @Test
    public void shouldPublishEventinCallRecord() {
        IVRRequest ivrRequest = new KookooRequest();
        ivrRequest.setSid("callId");
        ivrRequest.setEvent(IVREvent.NEW_CALL.key());
        KookooCallDetailRecord kooKooCallDetailRecord = new KookooCallDetailRecord(CallDetailRecord.create("callId", "phoneNumber"));

        when(allKooKooCallDetailRecords.findByCallId("callId")).thenReturn(kooKooCallDetailRecord);
        testEventAction.handleInternal(ivrRequest, request, response);

        ArgumentCaptor<KookooCallDetailRecord> kookooCallDetailRecordArgumentCaptor = ArgumentCaptor.forClass(KookooCallDetailRecord.class);
        verify(allKooKooCallDetailRecords).update(kookooCallDetailRecordArgumentCaptor.capture());

        KookooCallDetailRecord kookooCallDetailRecord = kookooCallDetailRecordArgumentCaptor.getValue();
        List<CallEvent> callEvents = kookooCallDetailRecord.getCallDetailRecord().getCallEvents();
        assertEquals(1, callEvents.size());
        assertEquals("NewCall", callEvents.get(0).getName());
    }

    private static class TestEventAction extends BaseEventAction {

        TestEventAction(AllKooKooCallDetailRecords allKooKooCallDetailRecords){
            super(null, null, allKooKooCallDetailRecords);
        }

        @Override
        public String handle(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
            return "";
        }
    }
}
