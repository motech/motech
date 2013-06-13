package ${groupId}.service.it;

import ${groupId}.service.HelloService;
import org.motechproject.testing.osgi.BaseOsgiIT;

public class HelloServiceBundleIT extends BaseOsgiIT {

    public void testThatHelloServiceIsAvailable(){

        HelloService helloService = (HelloService) applicationContext.getBean("testHelloService");
        assertNotNull(helloService);

        assertEquals("Hello",helloService.sayHello());
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testBlueprint.xml"};
    }

}