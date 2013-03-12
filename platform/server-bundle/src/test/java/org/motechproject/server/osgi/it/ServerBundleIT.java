package org.motechproject.server.osgi.it;

import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class ServerBundleIT extends BaseOsgiIT {


    public void testUIFrameworkService() throws IOException, InterruptedException {
        ServiceReference serviceReference = bundleContext.getServiceReference(UIFrameworkService.class.getName());
        assertNotNull(serviceReference);
        UIFrameworkService service = (UIFrameworkService) bundleContext.getService(serviceReference);
        assertNotNull(service);
        final ModuleRegistrationData registrationData = new ModuleRegistrationData("testUIFrameworkService", "/testUIFrameworkService");
        service.registerModule(registrationData);
        assertEquals(registrationData, service.getModuleData(registrationData.getModuleName()));
    }

    public void testThatControllerIsUp() throws IOException, InterruptedException {
        String response = new PollingHttpClient().get("http://localhost:8080/server/lang/list", new BasicResponseHandler());
        assertTrue(response.contains("en"));
    }

    @Override
    protected List<String> getImports() {
        // Packages in the test jar are excluded by default.
        return Arrays.asList("org.motechproject.server.ui");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testApplicationPlatformServerBundle.xml"};
    }
}
