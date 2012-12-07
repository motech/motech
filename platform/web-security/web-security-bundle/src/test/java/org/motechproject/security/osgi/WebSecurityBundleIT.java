package org.motechproject.security.osgi;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.server.config.settings.MotechSettings;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

public class WebSecurityBundleIT extends BaseOsgiIT {

   public void testWebSecurityService() {
       ServiceReference settingsReference = bundleContext.getServiceReference(PlatformSettingsService.class.getName());
       assertNotNull(settingsReference);
       PlatformSettingsService settings = (PlatformSettingsService) bundleContext.getService(settingsReference);
       assertNotNull(settings);
       ServiceReference eventService = bundleContext.getServiceReference(EventListenerRegistryService.class.getName());
       assertNotNull(eventService);
       EventListenerRegistryService event = (EventListenerRegistryService) bundleContext.getService(eventService);
       assertNotNull(event);
       settings.setActiveMqSetting("call.delay", "5000");

       settings.evictMotechSettingsCache();

       final MotechSettings platformSettings = settings.getPlatformSettings();
       final String delay = platformSettings.getActivemqProperties().getProperty("call.delay");
       assertEquals("5000", delay);

   }
}
