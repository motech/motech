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
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.dao.AppointmentsDAO;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.AppointmentWindow;
import org.motechproject.dao.MotechAuditableRepository;
import org.motechproject.eventgateway.EventGateway;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AppointmentsCouchDBDAOImpl extends MotechAuditableRepository<Appointment> implements AppointmentsDAO
{
    @Autowired
    private EventGateway eventGateway;

    @Autowired
    public AppointmentsCouchDBDAOImpl(@Qualifier("appointmentsDatabase") CouchDbConnector db) {
        super(Appointment.class, db);
        initStandardDesignDocument();
    }

    @Override
    public void addAppointment(Appointment appointment)
    {
        db.create(appointment);

        eventGateway.sendEventMessage(getSkinnyEvent(appointment, EventKeys.APPOINTMENT_CREATED_SUBJECT));
    }

    @Override
    public void updateAppointment(Appointment appointment)
    {
        db.update(appointment);

        eventGateway.sendEventMessage(getSkinnyEvent(appointment, EventKeys.APPOINTMENT_UPDATED_SUBJECT));
    }

    @Override
    public Appointment getAppointment(String appointmentId)
    {
        Appointment appointment = db.get(Appointment.class, appointmentId);
        return appointment;
    }

    @Override
    public void removeAppointment(String appointmentId)
    {
        Appointment appointment = getAppointment(appointmentId);

        removeAppointment(appointment);
    }

    @Override
    public void removeAppointment(Appointment appointment)
    {
        MotechEvent event = getSkinnyEvent(appointment, EventKeys.APPOINTMENT_DELETED_SUBJECT);

        db.delete(appointment);

        eventGateway.sendEventMessage(event);
    }

    @Override
    public void addAppointmentWindow(AppointmentWindow appointmentWindow)
    {
        db.create(appointmentWindow);

        eventGateway.sendEventMessage(getSkinnyEvent(appointmentWindow,
                                                     EventKeys.APPOINTMENT_WINDOW_CREATED_SUBJECT));
    }

    @Override
    public void updateAppointmentWindow(AppointmentWindow appointmentWindow)
    {
        db.update(appointmentWindow);

        eventGateway.sendEventMessage(getSkinnyEvent(appointmentWindow,
                                                     EventKeys.APPOINTMENT_WINDOW_UPDATED_SUBJECT));
    }

    @Override
    public AppointmentWindow getAppointmentWindow(String appointmentWindowId)
    {
        AppointmentWindow appointment = db.get(AppointmentWindow.class, appointmentWindowId);
        return appointment;
    }

    @Override
    public void removeAppointmentWindow(String appointmentWindowId)
    {
        AppointmentWindow appointment = getAppointmentWindow(appointmentWindowId);

        removeAppointmentWindow(appointment);
    }

    @Override
    public void removeAppointmentWindow(AppointmentWindow appointmentWindow)
    {
        MotechEvent event = getSkinnyEvent(appointmentWindow, EventKeys.APPOINTMENT_WINDOW_DELETED_SUBJECT);

        db.delete(appointmentWindow);

        eventGateway.sendEventMessage(event);
    }

    private MotechEvent getSkinnyEvent(Appointment apt, String subject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.APPOINTMENT_ID_KEY, apt.getId());

        MotechEvent event = new MotechEvent(subject, parameters);

        return event;
    }

    private MotechEvent getSkinnyEvent(AppointmentWindow aptWindow, String subject) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.APPOINTMENT_ID_KEY, aptWindow.getId());

        MotechEvent event = new MotechEvent(subject, parameters);

        return event;
    }

}
