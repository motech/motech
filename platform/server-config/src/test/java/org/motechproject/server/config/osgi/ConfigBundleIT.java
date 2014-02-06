package org.motechproject.server.config.osgi;

import org.junit.Test;
import org.motechproject.config.service.ConfigurationService;
import org.motechproject.testing.osgi.BaseOsgiIT;

public class ConfigBundleIT extends BaseOsgiIT {

    @Test
    public void testConfigBundle() throws Exception {
        ConfigurationService configurationService = (ConfigurationService) getApplicationContext().getBean("configurationService");
        assertNotNull(configurationService.loadBootstrapConfig().getCouchDbConfig().getUrl());
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testConfigBundleContext.xml"};
    }
}
