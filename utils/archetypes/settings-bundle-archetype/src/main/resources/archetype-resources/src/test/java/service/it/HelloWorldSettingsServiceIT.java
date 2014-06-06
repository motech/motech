#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.service.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.motechproject.testing.osgi.BasePaxIT;

import javax.inject.Inject;

import ${package}.service.HelloWorldSettingsService;
import org.osgi.framework.ServiceReference;

import static org.junit.Assert.assertNotNull;

/**
 * Verify that HelloWorldSettingsService is present, functional.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class HelloWorldSettingsServiceIT extends BasePaxIT {

    @Inject
    private HelloWorldSettingsService helloSettingsService;

    @Test
    public void testHelloWorldServicePresent() throws Exception {
        assertNotNull(helloSettingsService.getSettingsValue("${package}.sample.setting"));
        assertNotNull(helloSettingsService.getSettingsValue("${package}.bundle.name"));
        helloSettingsService.logInfoWithModuleSettings("test info message");
    }
}
