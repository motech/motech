package motech.archetype.service;

import org.motechproject.testing.osgi.BaseOsgiIT;

/**
 * Verify that the HelloWorld service template is present.
 */
public class HelloWorldServiceIT extends BaseOsgiIT {


    @Override
    protected String[] getConfigLocations() {
        return new String[] { "META-INF/spring/helloWorldServiceITContext.xml" };
    }
}
