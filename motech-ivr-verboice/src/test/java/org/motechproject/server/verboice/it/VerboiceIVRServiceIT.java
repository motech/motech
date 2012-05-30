package org.motechproject.server.verboice.it;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testVerboiceContext.xml"})
public class VerboiceIVRServiceIT {
    @Autowired
    private VerboiceIVRService verboiceIVRService;

    @Test
    @Ignore("run with verboice, softphone.. should receive call on softphone.")
    public void shouldInitiateCall(){
        CallRequest request = new CallRequest("1234",2000,"b");
        verboiceIVRService.initiateCall(request);
    }
}
