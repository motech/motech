package org.motechproject.server.osgi.it;

import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;

import java.io.IOException;


public class ServerBundleIT extends BaseOsgiIT {

    public void testUIFrameworkService() throws IOException, InterruptedException {
        UIFrameworkService service = (UIFrameworkService) verifyServiceAvailable(UIFrameworkService.class.getName());

        final ModuleRegistrationData registrationData = new ModuleRegistrationData("testUIFrameworkService", "/testUIFrameworkService");
        service.registerModule(registrationData);
        assertEquals(registrationData, service.getModuleData(registrationData.getModuleName()));
    }

    public void testThatControllerIsUp() throws IOException, InterruptedException {
        String response = new PollingHttpClient().get("http://localhost:8080/server/lang/list", new BasicResponseHandler());
        assertTrue(response.contains("en"));
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testApplicationPlatformServerBundle.xml"};
    }
}
