package org.motechproject.server.osgi;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.log4j.LogManager;
import org.junit.Test;
import org.osgi.framework.Bundle;

public class Log4JBundleLoaderTest {

    @Test
    public void loadBundleTest() throws Exception {
        String testConf = "/log4JBundleLoader.xml";
        Log4JBundleLoader loader = new Log4JBundleLoader();
        loader.setLog4jConf(testConf);

        Bundle bundle = mock(Bundle.class);
        when(bundle.getResource(testConf)).thenReturn(this.getClass().getResource(testConf));

        loader.loadBundle(bundle);

        assertNotNull(LogManager.getLoggerRepository().getRootLogger().getAppender("BUNDLE_LOADER_TEST"));
    }
}
