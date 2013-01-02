package org.motechproject.server.osgi.it;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ServerBundleIT extends BaseOsgiIT {

    public void testUIFrameworkService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(UIFrameworkService.class.getName());
        assertNotNull(serviceReference);
        UIFrameworkService service = (UIFrameworkService) bundleContext.getService(serviceReference);
        assertNotNull(service);
        final ModuleRegistrationData registrationData = new ModuleRegistrationData("testUIFrameworkService", "/testUIFrameworkService");
        service.registerModule(registrationData);
        assertEquals(registrationData, service.getModuleData(registrationData.getModuleName()));
    }

    public void testController() throws IOException {
        HttpClient client = new DefaultHttpClient();
        final String response = client.execute(new HttpGet("http://localhost:8080/server/lang"), new BasicResponseHandler());
        assertEquals("en", response);
    }

    @Override
    protected List<String> getImports() {
        // Packages in the test jar are excluded by default.
        return Arrays.asList("org.motechproject.server.ui");
    }
}
