package org.motechproject.mrs.osgi;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.testing.osgi.BaseOsgiIT;

public class MRSBundleIT extends BaseOsgiIT {

    public void testMRSApiBundle() {
        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));
    }
}
