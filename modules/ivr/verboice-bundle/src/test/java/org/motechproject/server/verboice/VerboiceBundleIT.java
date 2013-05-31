package org.motechproject.server.verboice;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.motechproject.ivr.service.contract.IVRService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.IOException;

public class VerboiceBundleIT extends BaseOsgiIT {

    public void testThatVerboiceIvrServicesIsAvailableOnImport() throws InvalidSyntaxException {
        ServiceReference[] references = bundleContext.getServiceReferences(IVRService.class.getName(), "(IvrProvider=Verboice)");
        assertNotNull(references);
        IVRService ivrService = (IVRService) bundleContext.getService(references[0]);
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
