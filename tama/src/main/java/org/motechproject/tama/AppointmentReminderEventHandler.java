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
package org.motechproject.tama;

import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.context.AppointmentReminderContext;
import org.motechproject.appointments.api.dao.AppointmentsDAO;
import org.motechproject.appointments.api.dao.RemindersDAO;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.context.Context;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.context.OutboxContext;
import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Handles Remind Appointment Events
 * 
 * todo implement tests
 * todo need to get clarity around what goes in server-common vs. server-api
 */
public class AppointmentReminderEventHandler implements EventListener {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

    public final static String TAMA_APPOINTMENT_REMINDER = "TamaAppointmentReminder";

    AppointmentsDAO appointmentsDAO = AppointmentReminderContext.getInstance().getAppointmentsDAO();
    RemindersDAO remindersDAO = AppointmentReminderContext.getInstance().getRemindersDAO();
    MetricsAgent metricsAgent = Context.getInstance().getMetricsAgent();

    OutboundVoiceMessageDao outboundVoiceMessageDao = OutboxContext.getInstance().getOutboundVoiceMessageDao();

    //Interim implementation
    String needToScheduleAppointmentVxmlUrl = "http://needtoschedule/";
    String upcomingAppointmentVxmlUrl = "";
    String missedAppointmentVxmlUrl = "";

    @Override
	public void handle(MotechEvent event) {
        metricsAgent.logEvent(event.getSubject());

        String appointmentId = EventKeys.getAppointmentId(event);
        if (appointmentId == null) {
            log.error("Can not handle the Appointment Reminder Event: " + event +
                     ". The event is invalid - missing the " + EventKeys.APPOINTMENT_ID_KEY + " parameter");
            return;
        }

        Appointment appointment = appointmentsDAO.getAppointment(appointmentId);
        if (appointment == null) {
            log.error("Can not handle the Appointment Reminder Event: " + event +
                     ". The event is invalid - no appointment for id " + appointmentId);
            return;
        }

        // I need to figure out what state the patient is in.  Either they need to schedule an appointment,
        // or their appointment is scheduled and they need to be reminded, or their appointment has past and they
        // need to be scolded.
        //
        // I also am making an assumption that the reminders have been set up according to the m, n logic.  So I
        // am not checking for that here.

        String url = null;
        if (null == appointment.getScheduledDate()) {
            url = needToScheduleAppointmentVxmlUrl + "?aptId=" + appointmentId + "&type=schedule";
        }

        Date today = new Date();
        if (null != appointment.getScheduledDate()) {

            // If they have visited the clinic disable this reminder
            if (null != appointment.getVisitId()) {
                Reminder reminder = remindersDAO.getReminder(EventKeys.getReminderId(event));

                if (null != reminder) {
                    reminder.setEnabled(false);
                    remindersDAO.updateReminder(reminder);
                }

                return;
            }

            if (today.compareTo(appointment.getScheduledDate()) <= 0) {
                url = upcomingAppointmentVxmlUrl + "?aptId=" + appointmentId + "&type=upcoming";
            } else {
                url = missedAppointmentVxmlUrl + "?aptId=" + appointmentId + "&type=missed";
            }
        }

        if (null != url) {
            VoiceMessageType mt = new VoiceMessageType();
            mt.setPriority(MessagePriority.MEDIUM);
            mt.setvXmlUrl(url);

            OutboundVoiceMessage msg = new OutboundVoiceMessage();
            msg.setPartyId(appointment.getExternalId());
            msg.setStatus(OutboundVoiceMessageStatus.PENDING);
            msg.setCreationTime(new Date());
            msg.setVoiceMessageType(mt);

            outboundVoiceMessageDao.add(msg);
        } else {
            log.warn(String.format("Unable to determine patient state for Appointment Reminder: AptId: %s Due Date: %s Scheduled Date: %s Today: %s",
                                   appointment, appointment.getDueDate(), appointment.getScheduledDate(), today));
        }
    }

    @Override
	public String getIdentifier() {
		return TAMA_APPOINTMENT_REMINDER;
	}

}
