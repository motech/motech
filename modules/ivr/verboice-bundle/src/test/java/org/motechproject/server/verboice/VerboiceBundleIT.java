package org.motechproject.server.verboice;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;

import java.io.IOException;

public class VerboiceBundleIT extends BaseOsgiIT {

    public void testThatVerboiceIvrServicesIsAvailableOnImport() {
        IVRService ivrService = (IVRService) getApplicationContext().getBean("testIvrServiceOsgi");
        assertNotNull(ivrService);
    }


    public void testThatVerboiceUrlIsAccessible() throws IOException, InterruptedException {
        HttpResponse response = new PollingHttpClient().get(
                String.format("http://localhost:%d/verboice/ivr?CallStatus=no-answer&CallSid=123A&From=12345",
                        TestContext.getJettyPort()));

        assertNotNull(response);
        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testIvrVerboiceOsgiContext.xml"};
    }
}
