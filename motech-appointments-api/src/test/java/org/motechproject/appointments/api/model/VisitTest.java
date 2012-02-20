package org.motechproject.appointments.api.model;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

public class VisitTest {
    
    @Test
    public void shouldAddAppointment_ToAVisit(){
        DateTime scheduledDate = DateUtil.now();
        Reminder reminder = new Reminder().startDate(scheduledDate.toDate()).endDate(scheduledDate.plusDays(1).toDate());
        Visit visit = new Visit().addAppointment(scheduledDate, reminder);

        assertNotNull(visit.appointment());
        assertEquals(DateUtil.today(), visit.appointment().dueDate().toLocalDate());
        assertNull(visit.appointment().scheduledDate());
        assertEquals(reminder, visit.appointmentReminder());
    }
}
