package org.motechproject.appointments.api.osgi;

import org.motechproject.appointments.api.service.AppointmentService;
import org.motechproject.appointments.api.service.contract.CreateVisitRequest;
import org.motechproject.appointments.api.service.contract.VisitResponse;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.UUID;

public class AppointmentsApiBundleIT extends BaseOsgiIT {

    public void testAppointmentService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(AppointmentService.class.getName());
        assertNotNull(serviceReference);
        AppointmentService appointmentService = (AppointmentService) bundleContext.getService(serviceReference);
        assertNotNull(appointmentService);

        final String externalId = "AppointmentsApiBundleIT-" + UUID.randomUUID();
        String visitName = "Visit-" + externalId;
        CreateVisitRequest request = new CreateVisitRequest().setVisitName(visitName);
        appointmentService.addVisit(externalId, request);
        VisitResponse response = appointmentService.findVisit(externalId, visitName);
        assertNotNull(response);
        assertEquals(visitName, response.getName());
        // Delete the doc in the post-integration phase
    }
}
