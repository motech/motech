package org.motechproject.osgi.web.repository;

import org.apache.log4j.LogManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.osgi.web.Log4JBundleLoader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testWebUtilApplication.xml"})
public class Log4JBundleLoaderIT {
    @Autowired
    Log4JBundleLoader loader;

    @Test
    public void loadBundleTest() throws Exception {
        String testConf = "/log4JBundleLoader.xml";
        loader.setLog4jConf(testConf);

        Bundle bundle = mock(Bundle.class);
        BundleContext bundleContext = mock(BundleContext.class);

        when(bundle.getResource(testConf)).thenReturn(this.getClass().getResource(testConf));
        when(bundle.getBundleContext()).thenReturn(bundleContext);

        when(bundleContext.getServiceReference("org.motechproject.event.listener.EventRelay")).thenReturn(null);

        loader.loadBundle(bundle);

        assertNotNull(LogManager.getLoggerRepository().getRootLogger().getAppender("BUNDLE_LOADER_TEST"));
    }
}
