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

import static org.motechproject.testing.utils.HttpServiceCheck.waitForLocalPortToListen;

public class CalllogBundleIT extends BaseOsgiIT{

    public static final String SERVER_URL = "http://localhost:8080";

    public void testCalllogSearch() throws IOException, InterruptedException {
        final ServiceReference serviceReference = bundleContext.getServiceReference(CalllogSearchService.class.getName());
        assertNotNull(serviceReference);
        waitForLocalPortToListen(8080, 30);
        HttpClient client = new DefaultHttpClient();
        final String response = client.execute(new HttpGet(SERVER_URL + "/callLog/search"), new BasicResponseHandler());
        assertTrue(new JsonParser().parse(response).isJsonArray());
    }


}
