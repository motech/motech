package org.motechproject.ivr.web;

import com.google.gson.JsonParser;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.decisiontree.server.service.CalllogSearchService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.io.IOException;

public class CalllogBundleIT extends BaseOsgiIT{

    public void testCalllogSearch() throws IOException {
        final ServiceReference serviceReference = bundleContext.getServiceReference(CalllogSearchService.class.getName());
        assertNotNull(serviceReference);

        HttpClient client = new DefaultHttpClient();
        final String response = client.execute(new HttpGet("http://localhost:8080/callLog/search"), new BasicResponseHandler());
        assertTrue(new JsonParser().parse(response).isJsonArray());
    }
}
