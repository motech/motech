package org.motechproject.server.osgi.it;

import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.motechproject.testing.utils.TestContext;

import java.io.IOException;


public class ServerBundleIT extends BaseOsgiIT {

    public void testUIFrameworkService() throws IOException, InterruptedException {
        UIFrameworkService uiFrameworkService = (UIFrameworkService) verifyServiceAvailable(UIFrameworkService.class.getName());
        assertNotNull(uiFrameworkService);

        final ModuleRegistrationData registrationData = new ModuleRegistrationData("testUIFrameworkService", "/testUIFrameworkService");
        uiFrameworkService.registerModule(registrationData);
        assertEquals(registrationData, uiFrameworkService.getModuleData(registrationData.getModuleName()));
    }

    public void testThatControllerIsUp() throws IOException, InterruptedException {
        String response = new PollingHttpClient().get(String.format("http://localhost:%d/server/lang/list",
                TestContext.getJettyPort()), new BasicResponseHandler());
        assertTrue(response.contains("en"));
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testApplicationPlatformServerBundle.xml"};
    }
}
