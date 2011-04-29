/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.appointments.api.dao.impl;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.dao.RemindersDAO;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.context.EventContext;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.event.EventRelay;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RemindersCouchDBDAOImpl extends MotechAuditableRepository<Reminder> implements RemindersDAO
{
    @Autowired(required = false)
    private EventRelay eventRelay = EventContext.getInstance().getEventRelay();

    @Autowired
    public RemindersCouchDBDAOImpl(@Qualifier("appointmentsDatabase") CouchDbConnector db) {
        super(Reminder.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void addReminder(Reminder reminder)
    {
        if (null == reminder.getAppointmentId()) {
            throw new IllegalArgumentException("Reminder must be associated with an appointment");
        }

        db.create(reminder);

        eventRelay.sendEventMessage(getSkinnyEvent(reminder, EventKeys.REMINDER_CREATED_SUBJECT));
    }

    @Override
    public void updateReminder(Reminder reminder)
    {
        if (null == reminder.getAppointmentId()) {
            throw new IllegalArgumentException("Reminder must be associated with an appointment");
        }

        db.update(reminder);

        eventRelay.sendEventMessage(getSkinnyEvent(reminder, EventKeys.REMINDER_UPDATED_SUBJECT));
    }

    @Override
    public void removeReminder(String reminderId)
    {
        Reminder reminder = getReminder(reminderId);

        removeReminder(reminder);
    }

    @Override
    public void removeReminder(Reminder reminder)
    {
        MotechEvent event = getSkinnyEvent(reminder, EventKeys.REMINDER_DELETED_SUBJECT);

        db.delete(reminder);

        eventRelay.sendEventMessage(event);
    }

    @Override
    public Reminder getReminder(String reminderId)
    {
        Reminder reminder = db.get(Reminder.class, reminderId);
        return reminder;
    }

    @Override
    @GenerateView
    public List<Reminder> findByAppointmentId(String appointmentId)
    {
        List<Reminder> ret = queryView("by_appointmentId", appointmentId);
        if (null == ret) {
            ret = Collections.<Reminder>emptyList();
        }
        return ret;
    }

    @Override
    @GenerateView
    public List<Reminder> findByExternalId(String externalId)
    {
        List<Reminder> ret = queryView("by_externalId", externalId);
        if (null == ret) {
            ret = Collections.<Reminder>emptyList();
        }
        return ret;
    }

    private MotechEvent getSkinnyEvent(Reminder reminder, String subject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.APPOINTMENT_ID_KEY, reminder.getAppointmentId());
        parameters.put(EventKeys.REMINDER_ID_KEY, reminder.getId());
        // Not sure I want this here, but it does save the handler from having to load the reminder
        parameters.put(EventKeys.JOB_ID_KEY, reminder.getJobId());

        MotechEvent event = new MotechEvent(subject, parameters);

        return event;
    }
}
