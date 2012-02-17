package org.motechproject.appointments.api.dao;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AllReminders extends MotechBaseRepository<Reminder> {
    @Autowired
    public AllReminders(@Qualifier("appointmentsDatabase") CouchDbConnector db) {
        super(Reminder.class, db);
    }

    @GenerateView
    public List<Reminder> findByReminderSubjectId(String appointmentId) {
        List<Reminder> reminders = queryView("by_reminderSubjectId", appointmentId);
        if (null == reminders) {
            reminders = Collections.<Reminder>emptyList();
        }
        return reminders;
    }

    @GenerateView
    public List<Reminder> findByExternalId(String externalId) {
        List<Reminder> reminders = queryView("by_externalId", externalId);
        if (null == reminders) {
            reminders = Collections.<Reminder>emptyList();
        }
        return reminders;
    }
}
