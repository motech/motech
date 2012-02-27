package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.mapper.VisitMapper;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AppointmentCalendarTest {

    private ReminderConfiguration reminderConfiguration;

    @Before
    public void setUp() {
        reminderConfiguration = new ReminderConfiguration().setRemindFrom(10).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
    }

    @Test
    public void updateVisit_ShouldAddNewVisit_AndRemoveExistingVisit() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();
        DateTime dueDate = DateTime.now();

        Visit scheduledVisit = new VisitMapper().map("week2", new VisitRequest().setDueDate(dueDate).setReminderConfiguration(reminderConfiguration));
        appointmentCalendar.addVisit(scheduledVisit);

        assertEquals(1, appointmentCalendar.visits().size());
        assertNotNull(appointmentCalendar.getVisit("week2"));

        Visit updatedVisit = new VisitMapper().map("week2", new VisitRequest().setDueDate(dueDate).setReminderConfiguration(reminderConfiguration)).visitDate(dueDate);
        appointmentCalendar.updateVisit(updatedVisit);

        assertEquals(1, appointmentCalendar.visits().size());
        assertEquals(updatedVisit, appointmentCalendar.getVisit("week2"));
        assertEquals(DateUtil.today(), appointmentCalendar.getVisit("week2").visitDate().toLocalDate());
    }

}
