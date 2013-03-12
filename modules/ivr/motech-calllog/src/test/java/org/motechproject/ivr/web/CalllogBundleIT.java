package org.motechproject.ivr.web;

import com.google.gson.JsonParser;
import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.decisiontree.server.service.CalllogSearchService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.osgi.framework.ServiceReference;

import java.io.IOException;

public class CalllogBundleIT extends BaseOsgiIT {


    public void testCalllogSearch() throws IOException, InterruptedException {
        final ServiceReference serviceReference = bundleContext.getServiceReference(CalllogSearchService.class.getName());
        assertNotNull(serviceReference);
        PollingHttpClient httpClient = new PollingHttpClient();
        String response = httpClient.get("http://localhost:8080/callLog/search", new BasicResponseHandler());
        assertTrue(new JsonParser().parse(response).isJsonArray());
    }

}
