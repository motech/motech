package mini.http.bundle.service.it;

import java.io.IOException;

import mini.http.bundle.service.HelloWorldService;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;
import org.apache.commons.httpclient.HttpStatus;

/**
 * Verify that HelloWorldService present, functional.
 */
public class HelloWorldServiceIT extends BaseOsgiIT {

    private PollingHttpClient httpClient = new PollingHttpClient(new DefaultHttpClient(), 5);

    public void testHelloWorldServicePresent() throws Exception {

        ServiceReference registryReference = bundleContext.getServiceReference(HelloWorldService.class.getName());
        assertNotNull(registryReference);
        HelloWorldService helloService = (HelloWorldService) bundleContext.getService(registryReference);
        assertNotNull(helloService);

        assertNotNull(helloService.sayHello());
    }

/*    public void testHelloWorldHttpPresent() throws IOException, InterruptedException {

        HttpGet httpGet = new HttpGet(String.format("http://localhost:%d/http-bundle/web-api/status",
                TestContext.getJettyPort()));

        addAuthHeader(httpGet, "motech", "motech");

        HttpResponse response = httpClient.execute(httpGet);

        assertNotNull(response);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }*/

    private void addAuthHeader(HttpGet httpGet, String userName, String password) {
        httpGet.addHeader("Authorization",
                "Basic " + new String(Base64.encodeBase64((userName + ":" + password).getBytes())));
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldServiceITContext.xml" };
    }
}
