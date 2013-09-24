package motech.archetype.service.it;

import java.util.Arrays;
import java.util.List;

import motech.archetype.service.HelloWorldService;

import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

/**
 * Verify that the HelloWorld service template is present.
 */
public class HelloWorldServiceIT extends BaseOsgiIT {

    public void testHelloWorldServicePresent() throws Exception {

        ServiceReference registryReference = bundleContext.getServiceReference(HelloWorldService.class.getName());
        assertNotNull(registryReference);
        HelloWorldService helloService = (HelloWorldService) bundleContext.getService(registryReference);
        assertNotNull(helloService);

        assertTrue(true);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldServiceITContext.xml" };
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("motech.archetype.service");
    }

}
