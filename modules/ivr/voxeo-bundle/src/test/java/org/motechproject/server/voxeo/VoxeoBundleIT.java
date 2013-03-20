package org.motechproject.server.voxeo;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.server.RequestInfo;
import org.motechproject.testing.utils.server.StubServer;

import java.io.IOException;

public class VoxeoBundleIT extends BaseOsgiIT {

    private static final String CONTEXT_PATH = "/SessionControl";
    private StubServer voxeoServer;

    @Override
    protected void onSetUp() throws Exception {
        voxeoServer = new StubServer(9998, CONTEXT_PATH);
        voxeoServer.start();
    }

    public void testThatVoxeoIVRServiceIsAvailable() {
        IVRService voxeoIVRService = (IVRService) getApplicationContext().getBean("voxeoIVRService");
        assertNotNull(voxeoIVRService);
    }


    public void testThatCCXmlGenerationUrlIsAccessible() throws IOException, InterruptedException {
        String response = new PollingHttpClient().get("http://localhost:8080/voxeo/ccxml", new BasicResponseHandler());
        assertTrue(response.contains("<ccxml version=\"1.0\">"));
    }


    public void testThatFlashUrlIsAccessible() throws IOException, InterruptedException {
        HttpResponse response = new PollingHttpClient().get("http://localhost:8080/voxeo/flash?phoneNumber=1233&applicationName=test");

        assertThatCallRequestWasMadeToVoxeoServer(voxeoServer.detailForRequest(CONTEXT_PATH));

        assertEquals(response.getStatusLine().getStatusCode(), HttpStatus.SC_OK);
    }

    private void assertThatCallRequestWasMadeToVoxeoServer(RequestInfo requestInfo) {
        assertNotNull(requestInfo);
        assertEquals("1233", requestInfo.getQueryParam("phonenum"));
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testVoxeoBundleContext.xml"};
    }

    @Override
    protected void onTearDown() throws Exception {
        voxeoServer.stop();
    }
}
