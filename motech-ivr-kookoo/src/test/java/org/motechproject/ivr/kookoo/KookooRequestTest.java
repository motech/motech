package org.motechproject.ivr.kookoo;

import org.junit.Test;
import org.motechproject.server.service.ivr.IVRRequest;
import org.motechproject.server.service.ivr.IVRSession;

import static junit.framework.Assert.assertEquals;

public class KookooRequestTest {
    @Test
    public void shouldFetchParamFromTamaData() {
        IVRRequest request = new KookooRequest();
        request.setParameter("hero", "batman");
        request.setParameter("villain", "joker");

        assertEquals("batman", request.getParameter("hero"));
        assertEquals("joker", request.getParameter("villain"));
    }

    @Test
    public void shouldGetInputWithoutPoundSymbol() {
        IVRRequest ivrRequest = new KookooRequest("sid", "cid", "someEvent", "4%23");

        assertEquals("4", ivrRequest.getInput());
    }

    @Test
    public void callDirectionShouldBeInbound_WhenThereIsNoTAMAData() {
        IVRRequest ivrRequest = new KookooRequest("sid", "cid", "someEvent", "4%23");
        assertEquals(IVRRequest.CallDirection.Inbound, ivrRequest.getCallDirection());
    }

    @Test
    public void callDirectionShouldBeInbound_WhenDirectionNotSpecifiedInTAMAData() {
        IVRRequest ivrRequest = new KookooRequest("sid", "cid", "someEvent", "4%23");
        assertEquals(IVRRequest.CallDirection.Inbound, ivrRequest.getCallDirection());
    }

    @Test
    public void callDirectionShouldBeOutbound_WhenDirectionSpecifiedInTAMAData() {
        IVRRequest ivrRequest = new KookooRequest("sid", "cid", "someEvent", "4%23");
        ivrRequest.setParameter("hero", "batman");
        ivrRequest.setParameter("villain", "joker");
        ivrRequest.setParameter(IVRSession.IVRCallAttribute.IS_OUTBOUND_CALL, "true");
        assertEquals(IVRRequest.CallDirection.Outbound, ivrRequest.getCallDirection());
    }
    
    @Test 
    public void shouldReadDataMapFromJsonString() {
    	String json = "{\"regimen_id\":\"23423423423\", \"dosage_id\":\"34324234\"}";
    	KookooRequest ivrRequest = new KookooRequest("sid", "cid", "someEvent", "4%23");
    	ivrRequest.setDataMap(json);
    	assertEquals("23423423423", ivrRequest.getParameter("regimen_id"));
    	assertEquals("34324234", ivrRequest.getParameter("dosage_id"));
    }

}
