package org.motechproject.mrs.osgi;

import org.motechproject.commons.api.DataProvider;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.mrs.MRSDataProvider;
import org.motechproject.mrs.domain.Facility;
import org.motechproject.mrs.domain.Patient;
import org.motechproject.mrs.domain.Person;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;

public class MRSBundleIT extends BaseOsgiIT {

    public void testMRSApiBundle() {
        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));

        ServiceReference serviceReference = bundleContext.getServiceReference(DataProvider.class.getName());
        assertNotNull(serviceReference);

        MRSDataProvider providerLookup = (MRSDataProvider) bundleContext.getService(serviceReference);
        assertNotNull(providerLookup);

        List<Class<?>> classes = Arrays.asList(Person.class, Patient.class, Facility.class);

        for (Class<?> cls : classes) {
            assertTrue(providerLookup.supports(cls.getSimpleName()));
        }
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testApplicationMrsBundle.xml"};
    }
}
