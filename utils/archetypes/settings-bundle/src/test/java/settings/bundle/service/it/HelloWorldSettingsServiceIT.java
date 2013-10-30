package settings.bundle.service.it;

import settings.bundle.service.HelloWorldSettingsService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

/**
 * Verify that HelloWorldSettingsService present, functional.
 */
public class HelloWorldSettingsServiceIT extends BaseOsgiIT {

    public void testHelloWorldServicePresent() throws Exception {

        ServiceReference registryReference = bundleContext.getServiceReference(HelloWorldSettingsService.class.getName());
        assertNotNull(registryReference);
        HelloWorldSettingsService helloSettingsService = (HelloWorldSettingsService) bundleContext.getService(registryReference);
        assertNotNull(helloSettingsService);

        assertNotNull(helloSettingsService.getSettingsValue("settings.bundle.sample.setting"));
        assertNotNull(helloSettingsService.getSettingsValue("settings.bundle.bundle.name"));
        helloSettingsService.logInfoWithModuleSettings("test info message");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldSettingsServiceITContext.xml" };
    }
}
