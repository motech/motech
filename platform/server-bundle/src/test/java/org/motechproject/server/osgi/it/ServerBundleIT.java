package org.motechproject.server.osgi.it;

import org.apache.http.impl.client.BasicResponseHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.TestContext;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ServerBundleIT extends BasePaxIT {

    @Inject
    private UIFrameworkService uiFrameworkService;

    @Override
    protected boolean startHttpServer() {
        return true;
    }

    @Test
    public void testUIFrameworkService() throws IOException, InterruptedException {
        final ModuleRegistrationData registrationData = new ModuleRegistrationData("testUIFrameworkService", "/testUIFrameworkService");
        uiFrameworkService.registerModule(registrationData);
        assertEquals(registrationData, uiFrameworkService.getModuleData(registrationData.getModuleName()));
    }

    @Test
    public void testThatControllerIsUp() throws IOException, InterruptedException {
        String response = getHttpClient().get(String.format("http://localhost:%d/server/lang/list",
                TestContext.getJettyPort()), new BasicResponseHandler());
        assertTrue(response.contains("en"));
    }
}
