package org.motechproject.server.kookoo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.motechproject.ivr.service.contract.IVRService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import java.io.IOException;

public class KookooIvrServiceBundleIT extends BaseOsgiIT {


    public void testThatIVRServiceIsAvailableForImport() throws InvalidSyntaxException {
        ServiceReference[] references = bundleContext.getServiceReferences(IVRService.class.getName(), "(IvrProvider=Kookoo)");
        assertNotNull(references);
        IVRService ivrService = (IVRService) bundleContext.getService(references[0]);
        assertNotNull(ivrService);
    }


    public void testThatKooKooServiceUrlIsAvailable() throws IOException, InterruptedException {
        HttpResponse response = new PollingHttpClient()
                .get(String.format("http://localhost:%d/kookoo/kookoo/status", TestContext.getJettyPort()));
        assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }

    public void testThatKooKooIvrIsAvailable() throws IOException, InterruptedException {
        HttpResponse response = new PollingHttpClient()
                .get(String.format("http://localhost:%d/kookoo/kookoo/ivr", TestContext.getJettyPort()));
        assertEquals(HttpStatus.SC_OK,response.getStatusLine().getStatusCode());
    }


    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testIVRKookooContext.xml"};
    }

}
