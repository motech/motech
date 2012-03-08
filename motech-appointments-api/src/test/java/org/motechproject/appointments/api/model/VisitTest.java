package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import java.util.Arrays;

import static junit.framework.Assert.*;

public class VisitTest {

    private DateTime scheduledDate;
    private Reminder reminder;
    private Visit visit;

    @Before
    public void setUp() {
        scheduledDate = DateUtil.now();
        createVisitWithAppointmentAndReminder();
    }

    @Test
    public void shouldAddAppointment_ToAVisit(){
        assertNotNull(visit.appointment());
        assertEquals(DateUtil.today(), visit.appointment().dueDate().toLocalDate());
        assertNull(visit.appointment().confirmedDate());
        assertEquals(reminder, visit.appointmentReminders().get(0));
    }

    @Test
    public void confirmingAnAppointment_ShouldCreateVisitReminder(){
        DateTime confirmedDate = scheduledDate.plusDays(10);
        Reminder visitReminder = new Reminder();

        visit.confirmAppointment(confirmedDate, visitReminder);

        assertEquals(visitReminder, visit.reminder());
        assertEquals(confirmedDate, visit.appointment().confirmedDate());
    }

    @Test
    public void markAVisitAsMissed(){
        assertFalse(visit.missed());

        visit.markAsMissed();
        assertTrue(visit.missed());
    }

    private void createVisitWithAppointmentAndReminder() {
        reminder = new Reminder().startDate(scheduledDate.toDate()).endDate(scheduledDate.plusDays(1).toDate());
        visit = new Visit().addAppointment(scheduledDate, Arrays.asList(reminder));
    }
}
