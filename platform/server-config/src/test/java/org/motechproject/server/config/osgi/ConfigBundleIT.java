package org.motechproject.server.config.osgi;

import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.ektorp.DbInfo;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

import java.util.jar.Manifest;

public class ConfigBundleIT extends BaseOsgiIT {
    @Override
    protected String getPlatformName() {
        return Platforms.EQUINOX;
    }

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
    protected Manifest getManifest() {
        StringBuilder builder = new StringBuilder();
        builder.append("org.motechproject.server.config");

        Manifest manifest = super.getManifest();
        String imports = manifest.getMainAttributes().getValue(Constants.IMPORT_PACKAGE);
        manifest.getMainAttributes().putValue(Constants.IMPORT_PACKAGE, builder.append(",").append(imports).toString());
        return manifest;
    }
}
