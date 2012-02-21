package org.motechproject.appointments.api.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.appointments.api.contract.ReminderConfiguration;
import org.motechproject.appointments.api.model.TypeOfVisit;
import org.motechproject.appointments.api.model.Visit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

public class VisitMapperTest {

    private DateTime today;
    private VisitMapper visitMapper;
    private ReminderConfiguration reminderConfiguration;

    @Before
    public void setUp() {
        reminderConfiguration = new ReminderConfiguration().setRemindFrom(10).setIntervalCount(1).setIntervalUnit(ReminderConfiguration.IntervalUnit.HOURS).setRepeatCount(20);
        today = DateTime.now();
        visitMapper = new VisitMapper();
    }

    @Test
    public void shouldMapToAScheduledVisit(){
        Visit visit = visitMapper.mapScheduledVisit(2, reminderConfiguration);

        assertEquals("week2", visit.name());
        assertEquals(TypeOfVisit.Scheduled, visit.typeOfVisit());
        assertNotNull(visit.appointment());
        assertEquals(today.plusWeeks(2).toLocalDate(), visit.appointment().dueDate().toLocalDate());
    }
}
