package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.CreateVisitRequest;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.Visit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class VisitMapperTest {

    private DateTime now;
    private VisitMapper visitMapper;
    private ReminderConfiguration reminderConfiguration;

    @Before
    public void setUp() {
        reminderConfiguration = new ReminderConfiguration().setRemindFrom(10).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
        now = DateTime.now();
        visitMapper = new VisitMapper();
    }

    @Test
    public void shouldMapAVisit_GivenNoAppointment() {
        CreateVisitRequest createVisitRequest = new CreateVisitRequest().setVisitName("baseline").setTypeOfVisit("Baseline");
        createVisitRequest.addData("key", "value");
        Visit visit = visitMapper.map(createVisitRequest);

        assertEquals("baseline", visit.name());
        assertEquals("Baseline", visit.typeOfVisit());
        assertNotNull(visit.appointment());
        assertNull(visit.appointmentReminder());
        assertEquals("value", visit.getData().get("key"));
    }

    @Test
    public void shouldMapAVisit_GivenAnAppointment() {
        DateTime dueDate = now.plusWeeks(2);
        CreateVisitRequest createVisitRequest = new CreateVisitRequest().setVisitName("week2").setTypeOfVisit("Scheduled").setAppointmentDueDate(dueDate).setAppointmentReminderConfiguration(reminderConfiguration);
        createVisitRequest.addData("key", "value");
        Visit visit = visitMapper.map(createVisitRequest);

        assertEquals("week2", visit.name());
        assertEquals("Scheduled", visit.typeOfVisit());
        assertNotNull(visit.appointment());
        assertNotNull(visit.appointmentReminder());
        assertEquals("value", visit.getData().get("key"));
        assertEquals(dueDate.toLocalDate(), visit.appointment().dueDate().toLocalDate());
    }
}
