package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.service.contract.CreateVisitRequest;
import org.motechproject.appointments.api.service.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.Visit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class VisitMapperTest {

    private DateTime now;
    private VisitMapper visitMapper;
    private ReminderConfiguration reminderConfiguration2;
    private ReminderConfiguration reminderConfiguration1;

    @Before
    public void setUp() {
        reminderConfiguration1 = new ReminderConfiguration().setRemindFrom(10).setIntervalCount(1)
                .setIntervalUnit(ReminderConfiguration.IntervalUnit.WEEKS).setRepeatCount(20);
        reminderConfiguration2 = new ReminderConfiguration().setRemindFrom(10).setIntervalCount(1)
                .setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
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
        assertEquals(0, visit.appointmentReminders().size());
        assertEquals("value", visit.getData().get("key"));
    }

    @Test
    public void shouldMapAVisit_GivenAnAppointment() {
        DateTime dueDate = now.plusWeeks(2);
        CreateVisitRequest createVisitRequest = new CreateVisitRequest().setVisitName("week2").setTypeOfVisit("Scheduled")
                .setAppointmentDueDate(dueDate).addAppointmentReminderConfiguration(reminderConfiguration1).addAppointmentReminderConfiguration(reminderConfiguration2);
        createVisitRequest.addData("key", "value");
        Visit visit = visitMapper.map(createVisitRequest);

        assertEquals(2, visit.appointmentReminders().size());
        assertEquals("week2", visit.name());
        assertEquals("Scheduled", visit.typeOfVisit());
        assertNotNull(visit.appointment());
        assertEquals("value", visit.getData().get("key"));
        assertEquals(dueDate.toLocalDate(), visit.appointment().dueDate().toLocalDate());
    }
}
