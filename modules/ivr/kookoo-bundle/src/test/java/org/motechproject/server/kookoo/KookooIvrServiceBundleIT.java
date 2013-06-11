package org.motechproject.server.kookoo;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.osgi.framework.InvalidSyntaxException;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

public class KookooIvrServiceBundleIT extends BaseOsgiIT {

    private PollingHttpClient httpClient = new PollingHttpClient();

    public void testThatIVRServiceIsAvailableForImport() throws InvalidSyntaxException {
        assertNotNull(applicationContext.getBean("testKookooIVRService"));
    }

    public void testKooKooCallbackUrlIsNotAuthenticated() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/kookoo/web-api/ivr", TestContext.getJettyPort()));

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    public void testKooKooStatusCallbackUrlIsNotAuthenticated() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/kookoo/web-api/ivr/callstatus", TestContext.getJettyPort()));

        HttpResponse response = httpClient.execute(httpGet);

        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Override
    protected List<String> getImports() {
        return asList("org.motechproject.ivr.service.contract");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testIVRKookooContext.xml"};
    }
}
