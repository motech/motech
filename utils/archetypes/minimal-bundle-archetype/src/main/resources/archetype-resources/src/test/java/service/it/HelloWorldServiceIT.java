#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.it;

import ${package}.service.HelloWorldService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

/**
 * Verify that HelloWorldService present, functional.
 */
public class HelloWorldServiceIT extends BaseOsgiIT {

    public void testHelloWorldServicePresent() throws Exception {

        ServiceReference registryReference = bundleContext.getServiceReference(HelloWorldService.class.getName());
        assertNotNull(registryReference);
        HelloWorldService helloService = (HelloWorldService) bundleContext.getService(registryReference);
        assertNotNull(helloService);

        assertNotNull(helloService.sayHello());
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldServiceITContext.xml" };
    }
}
