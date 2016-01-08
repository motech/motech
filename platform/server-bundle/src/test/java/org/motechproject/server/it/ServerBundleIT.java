package org.motechproject.server.it;

import org.apache.http.impl.client.BasicResponseHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.motechproject.server.web.helper.SuggestionHelper;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.motechproject.testing.utils.TestContext;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;
import org.springframework.web.context.WebApplicationContext;

import javax.inject.Inject;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ServerBundleIT extends BasePaxIT {

    @Inject
    private UIFrameworkService uiFrameworkService;

    @Inject
    private BundleContext bundleContext;

    @Override
    protected boolean shouldFakeModuleStartupEvent() {
        return false;
    }

    @Test
    public void testUIFrameworkService() throws IOException, InterruptedException {
        final ModuleRegistrationData registrationData = new ModuleRegistrationData("testUIFrameworkService", "/testUIFrameworkService");
        uiFrameworkService.registerModule(registrationData);
        assertEquals(registrationData, uiFrameworkService.getModuleData(registrationData.getModuleName()));
    }

    @Test
    public void shouldSuggestActivemqUrl() {
        WebApplicationContext context = ServiceRetriever.getWebAppContext(bundleContext, "org.motechproject.motech-platform-server-bundle");
        SuggestionHelper suggestionHelper = context.getBean(SuggestionHelper.class);

        assertEquals("tcp://localhost:61616", suggestionHelper.suggestActivemqUrl());
    }

    @Test
    public void testThatControllerIsUp() throws IOException, InterruptedException {
        String response = getHttpClient().get(String.format("http://localhost:%d/server/lang/list",
                TestContext.getJettyPort()), new BasicResponseHandler());
        assertTrue(response.contains("en"));
    }
}
