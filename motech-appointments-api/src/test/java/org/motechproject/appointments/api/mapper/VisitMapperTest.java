package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.contract.VisitRequest;
import org.motechproject.appointments.api.model.Visit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
    public void shouldMapAVisit() {
        DateTime dueDate = now.plusWeeks(2);
        VisitRequest visitRequest = new VisitRequest().setDueDate(dueDate).setReminderConfiguration(reminderConfiguration);
        Visit visit = visitMapper.map("week2", visitRequest);

        assertEquals("week2", visit.name());
        assertNotNull(visit.appointment());
        assertEquals(dueDate.toLocalDate(), visit.appointment().dueDate().toLocalDate());
    }

}
