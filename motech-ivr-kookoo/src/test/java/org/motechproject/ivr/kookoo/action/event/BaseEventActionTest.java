package org.motechproject.ivr.kookoo.action.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.ivr.kookoo.KookooRequest;
import org.motechproject.ivr.kookoo.domain.KookooCallDetailRecord;
import org.motechproject.ivr.kookoo.service.KookooCallDetailRecordsService;
import org.motechproject.server.service.ivr.CallDetailRecord;
import org.motechproject.server.service.ivr.IVREvent;
import org.motechproject.server.service.ivr.IVRRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

public class BaseEventActionTest extends BaseActionTest {

    private TestEventAction testEventAction;

    @Mock
    private KookooCallDetailRecordsService kookooCallDetailRecordsService;

    @Before
    public void setUp(){
        super.setUp();
        testEventAction = new TestEventAction(kookooCallDetailRecordsService);
    }

    @Test
    public void shouldPublishEventInCallRecord() {
        IVRRequest ivrRequest = new KookooRequest();
        ivrRequest.setEvent(IVREvent.NEW_CALL.key());

        KookooCallDetailRecord kooKooCallDetailRecord = new KookooCallDetailRecord(CallDetailRecord.newIncomingCallRecord("phoneNumber"));
        when(kookooCallDetailRecordsService.get("callId")).thenReturn(kooKooCallDetailRecord);

        testEventAction.handle("callId", ivrRequest, request, response);

        verify(kookooCallDetailRecordsService).appendEvent(Matchers.same("callId"), Matchers.same("NewCall"),anyMap());
    }

    private static class TestEventAction extends BaseEventAction {

        TestEventAction(KookooCallDetailRecordsService kookooCallDetailRecordsService){
            super(kookooCallDetailRecordsService);
        }

        @Override
        public String createResponse(IVRRequest ivrRequest, HttpServletRequest request, HttpServletResponse response) {
            return "";
        }
    }
}
