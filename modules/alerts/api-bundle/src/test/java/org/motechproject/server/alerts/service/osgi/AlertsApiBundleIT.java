package org.motechproject.server.alerts.service.osgi;

import org.motechproject.server.alerts.contract.AlertCriteria;
import org.motechproject.server.alerts.contract.AlertService;
import org.motechproject.server.alerts.domain.Alert;
import org.motechproject.server.alerts.domain.AlertStatus;
import org.motechproject.server.alerts.domain.AlertType;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.List;
import java.util.UUID;

public class AlertsApiBundleIT extends BaseOsgiIT {

    public void testAppointmentService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(AlertService.class.getName());
        assertNotNull(serviceReference);
        AlertService alertService = (AlertService) bundleContext.getService(serviceReference);
        assertNotNull(alertService);

        final String externalId = "AlertsApiBundleIT-" + UUID.randomUUID();
        alertService.create(externalId, null, "Description", AlertType.CRITICAL, AlertStatus.NEW, 1, null);
        List<Alert> alerts = alertService.search(new AlertCriteria().byExternalId(externalId));
        assertTrue(alerts != null && alerts.size() == 1);
        assertEquals(externalId, alerts.get(0).getExternalId());
        // Delete the doc in the post-integration phase
    }
}
