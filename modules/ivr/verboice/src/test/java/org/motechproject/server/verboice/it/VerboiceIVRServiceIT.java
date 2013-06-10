package org.motechproject.server.verboice.it;

import org.ektorp.CouchDbConnector;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ivr.service.contract.CallRequest;
import org.motechproject.server.verboice.VerboiceIVRService;
import org.motechproject.server.verboice.web.VerboiceIVRController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class VerboiceIVRServiceIT extends VerboiceTest {

    @Autowired
    private VerboiceIVRService verboiceIVRService;

    @Autowired
    VerboiceIVRController verboiceIVRController;

    @Test
    @Ignore("run with verboice")
    public void shouldInitiateCall(){
        CallRequest request = new CallRequest("1234",2000,"b");
        verboiceIVRService.initiateCall(request);
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return null;
    }
}
