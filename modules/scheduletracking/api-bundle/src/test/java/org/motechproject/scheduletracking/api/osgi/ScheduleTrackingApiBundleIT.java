package org.motechproject.scheduletracking.api.osgi;

import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.UUID;

public class ScheduleTrackingApiBundleIT extends BaseOsgiIT {

    public void testScheduleTrackingService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(ScheduleTrackingService.class.getName());
        assertNotNull(serviceReference);
        ScheduleTrackingService scheduleTrackingService = (ScheduleTrackingService) bundleContext.getService(serviceReference);
        assertNotNull(scheduleTrackingService);

        final String scheduleName = "ScheduleTrackingApiBundleIT-" + UUID.randomUUID();
        try {
            scheduleTrackingService.add("{name: " + scheduleName + "}");
            Schedule schedule = scheduleTrackingService.getScheduleByName(scheduleName);
            assertNotNull(schedule);
            assertEquals(scheduleName, schedule.getName());
        } finally {
            scheduleTrackingService.remove(scheduleName);
        }

    }
}
