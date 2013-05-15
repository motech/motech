package org.motechproject.ivr.web;

import com.google.gson.JsonParser;
import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;

import java.io.IOException;

public class CalllogBundleIT extends BaseOsgiIT {

    public void testCalllogSearch() throws IOException, InterruptedException {
        PollingHttpClient httpClient = new PollingHttpClient();
        String response = httpClient.get(String.format("http://localhost:%d/ivr/api/calllog/search", TestContext.getJettyPort()),
                new BasicResponseHandler());

        assertTrue(new JsonParser().parse(response).isJsonArray());
    }
}
