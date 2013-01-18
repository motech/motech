package org.motechproject.sms.smpp.osgi;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.testing.osgi.BaseOsgiIT;

public class SmsSmppServiceBundleIT extends BaseOsgiIT {


    public void testSmsSmppServiceBundle() throws Exception {

        assertNotNull(bundleContext.getServiceReference(SmsService.class.getName()));

        SmsService smsService = (SmsService) getApplicationContext().getBean("smsServiceRef");
        assertNotNull(smsService);

        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));

        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));

        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));

        assertNotNull(bundleContext.getServiceReference(PlatformSettingsService.class.getName()));

    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testSmsSmppBundleContext.xml"};
    }


}
