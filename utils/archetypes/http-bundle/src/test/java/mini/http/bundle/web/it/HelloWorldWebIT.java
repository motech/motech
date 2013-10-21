package mini.http.bundle.web.it;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;

/**
 * Verify that HelloWorldService is present and functional.
 */
public class HelloWorldWebIT extends BaseOsgiIT {
    private static final String ADMIN_USERNAME = "motech";
    private static final String ADMIN_PASSWORD = "motech";

    private PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 5);

    public void testHelloWorldGetRequest() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/http-bundle/sayHello",
                TestContext.getJettyPort()));
        addAuthHeader(httpGet, ADMIN_USERNAME, ADMIN_PASSWORD);

        HttpResponse response = httpClient.execute(httpGet);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    public void testStatusGetRequest() throws IOException, InterruptedException {
        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/http-bundle/web-api/status",
                TestContext.getJettyPort()));
        addAuthHeader(httpGet, ADMIN_USERNAME, ADMIN_PASSWORD);

        HttpResponse response = httpClient.execute(httpGet);
        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    private void addAuthHeader(HttpGet httpGet, String userName, String password) {
        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldServiceITContext.xml" };
    }

    @Override
    protected List<String> getImports() {
        return asList("mini.http.bundle.service");
    }
}
