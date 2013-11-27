#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.it;

import ${package}.service.HelloWorldSettingsService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

/**
 * Verify that HelloWorldSettingsService is present, functional.
 */
public class HelloWorldSettingsServiceIT extends BaseOsgiIT {

    public void testHelloWorldServicePresent() throws Exception {

        ServiceReference registryReference = bundleContext.getServiceReference(HelloWorldSettingsService.class.getName());
        assertNotNull(registryReference);
        HelloWorldSettingsService helloSettingsService = (HelloWorldSettingsService) bundleContext.getService(registryReference);
        assertNotNull(helloSettingsService);

        assertNotNull(helloSettingsService.getSettingsValue("${package}.sample.setting"));
        assertNotNull(helloSettingsService.getSettingsValue("${package}.bundle.name"));
        helloSettingsService.logInfoWithModuleSettings("test info message");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldSettingsServiceITContext.xml" };
    }
}
