package org.motechproject.ivr.kookoo;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.service.ivr.CallDirection;
import org.motechproject.server.service.ivr.IVREvent;

import static junit.framework.Assert.assertEquals;

public class KookooRequestTest {
    private KookooRequest ivrRequest;

    @Before
    public void setUp() {
        ivrRequest = new KookooRequest("sid", "cid", "someEvent", "4#");
    }

    @Test
    public void shouldGetInputWithoutPoundSymbol() {
        assertEquals("4", ivrRequest.getInput());
    }

    @Test
    public void callDirectionShouldBeInbound_WhenThereIsNoTAMAData() {
        assertEquals(CallDirection.Inbound, ivrRequest.getCallDirection());
    }

    @Test
    public void callDirectionShouldBeInbound_WhenDirectionNotSpecifiedInTAMAData() {
        assertEquals(CallDirection.Inbound, ivrRequest.getCallDirection());
    }

    @Test
    public void callDirectionShouldBeOutbound_WhenDirectionSpecifiedInTAMAData() {
        ivrRequest.setParameter("hero", "batman");
        ivrRequest.setParameter("villain", "joker");
        ivrRequest.setParameter(KookooCallServiceImpl.IS_OUTBOUND_CALL, "true");
        assertEquals(CallDirection.Outbound, ivrRequest.getCallDirection());
    }

    @Test
    public void shouldReadDataMapFromJsonString() {
        String json = "{\"regimen_id\":\"23423423423\", \"dosage_id\":\"34324234\"}";
        ivrRequest.setDataMap(json);
        assertEquals("23423423423", ivrRequest.getParameter("regimen_id"));
        assertEquals("34324234", ivrRequest.getParameter("dosage_id"));
    }

    @Test
    public void setDefaults_ThisIsToHandleScenariosWhenKooKooCallsWithoutAnyParameters() {
        ivrRequest = new KookooRequest();
        ivrRequest.setDefaults();
        assertEquals(IVREvent.GotDTMF.toString(), ivrRequest.getEvent());
        assertEquals("", ivrRequest.getData());
    }
}
