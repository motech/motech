package org.motechproject.server.impl;

import org.apache.log4j.LogManager;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Log4JBundleLoaderTest {

    @Test
    public void loadBundleTest() throws Exception {
        String testConf = "/log4JBundleLoader.xml";
        Log4JBundleLoader loader = new Log4JBundleLoader();
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
