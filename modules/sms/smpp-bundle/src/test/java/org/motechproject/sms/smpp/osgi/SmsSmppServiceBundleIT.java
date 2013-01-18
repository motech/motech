package org.motechproject.sms.smpp.osgi;

import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.testing.osgi.BaseOsgiIT;

import java.io.IOException;

public class SmsSmppServiceBundleIT extends BaseOsgiIT {


    public void testSmsSmppServiceBundle() throws Exception {


        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));

        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));

        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));

        assertNotNull(bundleContext.getServiceReference(PlatformSettingsService.class.getName()));

        assertNotNull(bundleContext.getServiceReference(SmsService.class.getName()));

    }


}
