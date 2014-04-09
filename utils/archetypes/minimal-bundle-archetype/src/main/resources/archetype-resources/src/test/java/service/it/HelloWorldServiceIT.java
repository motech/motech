#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.it;

import ${package}.service.HelloWorldService;
import org.junit.Test;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.motechproject.testing.osgi.BasePaxIT;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;

/**
 * Verify that HelloWorldService present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class HelloWorldServiceIT extends BasePaxIT {

    @Inject
    HelloWorldService helloService;

    @Test
    public void testHelloWorldServicePresent() throws Exception {
        assertNotNull(helloService);
        assertNotNull(helloService.sayHello());
    }
}
