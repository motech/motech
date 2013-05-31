package org.motechproject.ivr.osgi;

import com.google.gson.JsonParser;
import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.ivr.service.contract.CallRecordsService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.osgi.framework.ServiceReference;

import java.io.IOException;

public class CalllogBundleIT extends BaseOsgiIT {

    public void testCalllogSearch() throws IOException, InterruptedException {
        PollingHttpClient httpClient = new PollingHttpClient();
        String response = httpClient.get(String.format("http://localhost:%d/ivr/api/calllog/search", TestContext.getJettyPort()),
                new BasicResponseHandler());

        assertTrue(new JsonParser().parse(response).isJsonArray());
    }

    public void testThatCallRecordsServiceIsAvailable() {
        ServiceReference reference = bundleContext.getServiceReference(CallRecordsService.class.getName());
        assertNotNull(reference);
        CallRecordsService service = (CallRecordsService) bundleContext.getService(reference);
        assertNotNull(service);
    }

//    @Override
//    protected List<String> getImports() {
//        return Arrays.asList("org.motechproject.ivr.service", "org.motechproject.ivr.repository");
//    }
}
