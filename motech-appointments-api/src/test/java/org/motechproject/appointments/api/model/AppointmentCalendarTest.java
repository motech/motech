package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.mapper.VisitMapper;
import org.motechproject.util.DateUtil;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class AppointmentCalendarTest {

    private ReminderConfiguration reminderConfiguration;

    @Before
    public void setUp() {
        reminderConfiguration = new ReminderConfiguration().setRemindFrom(10).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
    }

    @Test
    public void shouldCreateBaselineVisit_OnCreatingA_NewAppointmentCalender() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();

        List<Visit> visits = appointmentCalendar.visits();
        assertEquals(1, visits.size());
        assertEquals(TypeOfVisit.Baseline, visits.get(0).typeOfVisit());
        assertEquals("baseline", visits.get(0).name());
    }

    @Test
    public void shouldGetBaselineVisit() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();

        Visit baselineVisit = appointmentCalendar.baselineVisit();
        assertNotNull(baselineVisit);
        assertEquals(TypeOfVisit.Baseline, baselineVisit.typeOfVisit());
    }

    @Test
    public void updateVisit_ShouldAddNewVisit_AndRemoveExistingVisit() {
        AppointmentCalendar appointmentCalendar = new AppointmentCalendar();

        Visit scheduledVisit = new VisitMapper().mapScheduledVisit(2, reminderConfiguration);
        appointmentCalendar.addVisit(scheduledVisit);

        assertEquals(2, appointmentCalendar.visits().size());
        assertNotNull(appointmentCalendar.getVisit("baseline"));
        assertNotNull(appointmentCalendar.getVisit("week2"));

        Visit updatedVisit = new VisitMapper().mapScheduledVisit(2, reminderConfiguration).visitDate(DateTime.now());
        appointmentCalendar.updateVisit(updatedVisit);

        assertEquals(2, appointmentCalendar.visits().size());
        assertEquals(updatedVisit, appointmentCalendar.getVisit("week2"));
        assertEquals(DateUtil.today(), appointmentCalendar.getVisit("week2").visitDate().toLocalDate());
    }
}
