package repository.bundle.service.it;

import repository.bundle.service.HelloWorldRecordService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

/**
 * Verify that HelloWorldRecordService present, functional.
 */
public class HelloWorldRecordServiceIT extends BaseOsgiIT {

    public void testHelloWorldRecordServicePresent() throws Exception {

        ServiceReference registryReference = bundleContext.getServiceReference(HelloWorldRecordService.class.getName());
        assertNotNull(registryReference);
        HelloWorldRecordService helloService = (HelloWorldRecordService) bundleContext.getService(registryReference);
        assertNotNull(helloService);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldRecordServiceITContext.xml" };
    }
}
