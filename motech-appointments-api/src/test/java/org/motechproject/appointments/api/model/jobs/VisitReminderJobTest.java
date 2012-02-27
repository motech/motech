package org.motechproject.appointments.api.model.jobs;

import org.junit.Test;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.appointments.api.model.Visit;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;

public class VisitReminderJobTest {

    private Visit visit;
    private VisitReminderJob visitReminderJob;

    public VisitReminderJobTest() {
        visit = new Visit().appointment(new Appointment()).reminder(new Reminder());
        visitReminderJob = new VisitReminderJob("externalId", DateUtil.today().toDate(), visit);
    }

    @Test
    public void shouldRunEveryDay() {
        assertEquals("0 0 0 ? * *", visitReminderJob.getCronExpression());
    }
}