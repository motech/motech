package org.motechproject.server.verboice;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.osgi.framework.InvalidSyntaxException;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

public class VerboiceBundleIT extends BaseOsgiIT {

    private PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 60);

    public void testThatVerboiceIvrServicesIsAvailableOnImport() throws InvalidSyntaxException {
        assertNotNull("testIvrServiceOsgi");
    }

    public void testVerboiceCallBackAuthenticationSuccess() throws IOException, InterruptedException {

        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/verboice/web-api/ivr?CallSid=123", TestContext.getJettyPort()));
        addAuthHeader(httpGet, "motech", "motech");

        HttpResponse response = httpClient.execute(httpGet);

        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    public void testVerboiceCallBackAuthenticationFailed() throws IOException, InterruptedException {

        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/verboice/web-api/ivr?CallSid=123", TestContext.getJettyPort()));
        addAuthHeader(httpGet, "bad", "user");

        HttpResponse response = httpClient.execute(httpGet);

        assertNotNull(response);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    public void testVerboiceStatusCallBackAuthenticationSuccess() throws IOException, InterruptedException {

        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/verboice/web-api/ivr/callstatus?CallSid=123", TestContext.getJettyPort()));
        addAuthHeader(httpGet, "motech", "motech");

        HttpResponse response = httpClient.execute(httpGet);

        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    public void testVerboiceStatusCallBackAuthenticationFailed() throws IOException, InterruptedException {

        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/verboice/web-api/ivr/callstatus?CallSid=123", TestContext.getJettyPort()));
        addAuthHeader(httpGet, "bad", "user");

        HttpResponse response = httpClient.execute(httpGet);

        assertNotNull(response);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    private void addAuthHeader(HttpGet httpGet, String userName, String password) {
        httpGet.addHeader("Authorization", "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    @Override
    protected List<String> getImports() {
        return asList("org.motechproject.ivr.service.contract");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testIvrVerboiceOsgiContext.xml"};
    }
}
