package org.motechproject.ivr.web;

import com.google.gson.JsonParser;
import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.decisiontree.server.service.CalllogSearchService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.io.IOException;

public class CalllogBundleIT extends BaseOsgiIT{

    private static final String HOST = "localhost";
    private static final int PORT = 8080;

    public void testCalllogSearch() throws IOException, InterruptedException {
        final ServiceReference serviceReference = bundleContext.getServiceReference(CalllogSearchService.class.getName());
        assertNotNull(serviceReference);
        String response = executeHttpCall(HOST,PORT,  "/callLog/search", new BasicResponseHandler());
        assertTrue(new JsonParser().parse(response).isJsonArray());
    }

}
