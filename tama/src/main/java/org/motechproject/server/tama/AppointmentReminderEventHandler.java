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
package org.motechproject.server.tama;

import org.motechproject.appointments.api.AppointmentService;
import org.motechproject.appointments.api.EventKeys;
import org.motechproject.appointments.api.ReminderService;
import org.motechproject.appointments.api.context.AppointmentsContext;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Reminder;
import org.motechproject.context.Context;
import org.motechproject.metrics.MetricsAgent;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.VoiceOutboxService;
import org.motechproject.outbox.api.context.OutboxContext;
import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.UUID;

/**
 * Handles Remind Appointment Events
 * 
 * todo implement tests
 * todo need to get clarity around what goes in server-common vs. server-api
 */
public class AppointmentReminderEventHandler {
    
    //TODO: move this out to config somewhere
    private static final String HOST_IP = "10.0.1.6";
    private static final String OUTBOX_MESSAGE_BASE_URL = "http://" + HOST_IP + "/motech-platform-server/module/outbox/vxml/outboxMessage";
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    AppointmentService appointmentService = AppointmentsContext.getInstance().getAppointmentService();
    ReminderService reminderService = AppointmentsContext.getInstance().getReminderService();
    MetricsAgent metricsAgent = Context.getInstance().getMetricsAgent();

    VoiceOutboxService voiceOutboxService = OutboxContext.getInstance().getVoiceOutboxService();

    //Interim implementation
    String needToScheduleAppointmentVxmlUrl = OUTBOX_MESSAGE_BASE_URL;
    String upcomingAppointmentVxmlUrl = OUTBOX_MESSAGE_BASE_URL;
    String missedAppointmentVxmlUrl = OUTBOX_MESSAGE_BASE_URL;
    
    @MotechListener(subjects={EventKeys.APPOINTMENT_REMINDER_EVENT_SUBJECT})
	public void handle(MotechEvent event) {
// 		Metrics is logged for every handler automatically
//      metricsAgent.logEvent(event.getSubject());
        if (appointmentService == null) {
            appointmentService = AppointmentsContext.getInstance().getAppointmentService();
        }
        
        if (voiceOutboxService == null) {
            voiceOutboxService = OutboxContext.getInstance().getVoiceOutboxService();
        }
        
        if (reminderService == null) {
            reminderService = AppointmentsContext.getInstance().getReminderService();
        }
        
        String appointmentId = EventKeys.getAppointmentId(event);
        if (appointmentId == null) {
            log.error("Can not handle the Appointment Reminder Event: " + event +
                     ". The event is invalid - missing the " + EventKeys.APPOINTMENT_ID_KEY + " parameter");
            return;
        }

        Appointment appointment = appointmentService.getAppointment(appointmentId);
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
        
        String msgId = UUID.randomUUID().toString();
        String pId = appointment.getExternalId();

        String url = null;
        if (null == appointment.getScheduledDate()) {
            url = needToScheduleAppointmentVxmlUrl + "?mId=" + msgId + "&pId=" + pId + "&type=schedule";
        }

        Date today = new Date();
        if (null != appointment.getScheduledDate()) {

            // If they have visited the clinic disable this reminder
            if (null != appointment.getVisitId()) {
                Reminder reminder = reminderService.getReminder(EventKeys.getReminderId(event));

                if (null != reminder) {
                    reminder.setEnabled(false);
                    reminderService.updateReminder(reminder);
                }

                return;
            }

            if (today.compareTo(appointment.getScheduledDate()) <= 0) {
                url = upcomingAppointmentVxmlUrl + "?mId=" + msgId + "&pId=" + pId + "&type=upcoming";
            } else {
                url = missedAppointmentVxmlUrl + "?mId=" + msgId + "&pId=" + pId + "&type=missed";
            }
        }

        if (null != url) {
            VoiceMessageType mt = new VoiceMessageType();
            mt.setPriority(MessagePriority.MEDIUM);
            mt.setvXmlTemplateName(url);

            OutboundVoiceMessage msg = new OutboundVoiceMessage();
            msg.setId(msgId);
            msg.setPartyId(appointment.getExternalId());
            msg.setStatus(OutboundVoiceMessageStatus.PENDING);
            msg.setCreationTime(new Date());
            msg.setVoiceMessageType(mt);

            voiceOutboxService.addMessage(msg);
        } else {
            log.warn(String.format("Unable to determine patient state for Appointment Reminder: AptId: %s Due Date: %s Scheduled Date: %s Today: %s",
                                   appointment, appointment.getDueDate(), appointment.getScheduledDate(), today));
        }
    }
}
