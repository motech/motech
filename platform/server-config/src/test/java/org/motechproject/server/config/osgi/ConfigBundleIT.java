package org.motechproject.server.config.osgi;

import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;

public class ConfigBundleIT extends BaseOsgiIT {

    public void testConfigBundle() throws Exception {
        ServiceReference settingsReference = bundleContext.getServiceReference(PlatformSettingsService.class.getName());
        assertNotNull(settingsReference);
        PlatformSettingsService settings = (PlatformSettingsService) bundleContext.getService(settingsReference);
        assertNotNull(settings);
        settings.setActiveMqSetting("call.delay", "5000");

        settings.evictMotechSettingsCache();

        final MotechSettings platformSettings = settings.getPlatformSettings();
        final String delay = platformSettings.getActivemqProperties().getProperty("call.delay");
        assertEquals("5000", delay);
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.server.config", "org.motechproject.commons.couchdb.service");
    }
}
