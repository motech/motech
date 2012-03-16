package it;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.server.voxeo.VoxeoIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:voxeoResources.xml"})
public class VoxeoOutboundCallIt {
    @Autowired
    private IVRService voxeoIvrService;

    @Test
    @Ignore("run with phone setup at 1234@localhost:5080; config in voxeo-config.json")
    public void shouldInitiateAnOutBoundCall() {
        CallRequest callRequest = new CallRequest("1234@localhost:5080", 2, "");
        HashMap<String, String> payload = new HashMap<String, String>();
        payload.put("applicationName", "platform");
        callRequest.setPayload(payload);
        voxeoIvrService.initiateCall(callRequest);
    }
}
