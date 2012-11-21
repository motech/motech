package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.commons.date.util.DateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

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
    public void shouldAddAppointment_ToAVisit() {
        assertNotNull(visit.appointment());
        assertEquals(DateUtil.today(), visit.appointment().dueDate().toLocalDate());
        assertNull(visit.appointment().confirmedDate());
        assertEquals(reminder, visit.appointmentReminders().get(0));
    }

    @Test
    public void confirmingAnAppointment_ShouldCreateVisitReminder() {
        DateTime confirmedDate = scheduledDate.plusDays(10);
        Reminder visitReminder = new Reminder();

        visit.confirmAppointment(confirmedDate, visitReminder);

        assertEquals(visitReminder, visit.reminder());
        assertEquals(confirmedDate, visit.appointment().confirmedDate());
    }

    @Test
    public void markAVisitAsMissed() {
        assertFalse(visit.missed());

        visit.markAsMissed();
        assertTrue(visit.missed());
    }

    @Test
    public void shouldTestEquality() {
        final DateTime scheduleStartDate = newDateTime(2012, 2, 3, 1, 2, 3);
        Reminder reminder1 = new Reminder().startDate(scheduleStartDate.plusDays(2).toDate()).endDate(scheduleStartDate.plusDays(2).toDate());
        Reminder reminder2 = new Reminder().startDate(scheduleStartDate.plusWeeks(5).toDate()).endDate(scheduleStartDate.plusWeeks(8).toDate()).repeatCount(3)
                .intervalSeconds(100000);
        Visit visit1 = new Visit().addAppointment(scheduleStartDate, Arrays.asList(reminder1, reminder2)).addData("new", new Reminder());
        Visit visitDuplicate = new Visit().addAppointment(scheduleStartDate, Arrays.asList(reminder1, reminder2)).addData("new", new Reminder());

        assertTrue(visit1.isSame(visitDuplicate));
        assertTrue(visitDuplicate.isSame(visit1));
        assertTrue(new Visit().isSame(new Visit()));
        assertTrue(new Visit().name("A").isSame(new Visit().name("A")));
        assertTrue(new Visit().typeOfVisit("AA").isSame(new Visit().typeOfVisit("AA")));
        assertTrue(new Visit().markAsMissed().isSame(new Visit().markAsMissed()));
        assertTrue(new Visit().visitDate(newDateTime(2012, 3, 2, 2, 2, 2)).isSame(new Visit().visitDate(newDateTime(2012, 3, 2, 2, 2, 2))));
        assertTrue(new Visit().reminder(new Reminder()).isSame(new Visit().reminder(new Reminder())));
        assertTrue(new Visit().addAppointment(newDateTime(2012, 2, 1, 3, 2, 2), Collections.<Reminder>emptyList())
                .isSame(new Visit().addAppointment(newDateTime(2012, 2, 1, 3, 2, 2), Collections.<Reminder>emptyList())));
        assertTrue(new Visit().isSame(new Visit().addData(new HashMap())));

        assertFalse(new Visit().name("A").isSame(new Visit().name("AA")));
        assertFalse(new Visit().typeOfVisit("AA").isSame(new Visit().typeOfVisit("A")));
        assertFalse(new Visit().visitDate(newDateTime(2012, 3, 2, 2, 2, 12)).isSame(new Visit().visitDate(newDateTime(2012, 3, 2, 2, 2, 2))));
        assertFalse(new Visit().reminder(new Reminder().repeatCount(2)).isSame(new Visit().reminder(new Reminder())));
        assertFalse(new Visit().addAppointment(newDateTime(2012, 2, 11, 3, 2, 2), Collections.<Reminder>emptyList())
                .isSame(new Visit().addAppointment(newDateTime(2012, 4, 1, 3, 2, 2), Collections.<Reminder>emptyList())));
        assertFalse(new Visit().addData("A", "B").isSame(new Visit().addData(new HashMap())));
        assertFalse(new Visit().addData("A", reminder1).isSame(new Visit().addData("A", reminder2)));
    }

    private void createVisitWithAppointmentAndReminder() {
        reminder = new Reminder().startDate(scheduledDate.toDate()).endDate(scheduledDate.plusDays(1).toDate());
        visit = new Visit().addAppointment(scheduledDate, Arrays.asList(reminder));
    }
}
