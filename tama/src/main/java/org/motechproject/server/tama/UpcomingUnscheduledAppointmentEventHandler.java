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

import org.motechproject.appointments.api.appointmentreminder.EventKeys;
import org.motechproject.appointments.api.appointmentreminder.context.AppointmentReminderContext;
import org.motechproject.appointments.api.dao.PatientDAO;
import org.motechproject.appointments.api.model.Appointment;
import org.motechproject.appointments.api.model.Patient;
import org.motechproject.model.MotechEvent;
import org.motechproject.outbox.api.context.OutboxContext;
import org.motechproject.outbox.api.dao.OutboundVoiceMessageDao;
import org.motechproject.outbox.api.model.MessagePriority;
import org.motechproject.outbox.api.model.OutboundVoiceMessage;
import org.motechproject.outbox.api.model.OutboundVoiceMessageStatus;
import org.motechproject.outbox.api.model.VoiceMessageType;
import org.motechproject.server.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Handles Remind Appointment Events
 * 
 *
 */
public class UpcomingUnscheduledAppointmentEventHandler implements EventListener {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

    public final static String UPCOMING_UNSCHEDULED = "UpcomingUnscheduledAppointment";

    PatientDAO patientDao = AppointmentReminderContext.getInstance().getPatientDAO();

    OutboundVoiceMessageDao outboundVoiceMessageDao = OutboxContext.getInstance().getOutboundVoiceMessageDao();

    //Interim implementation
    String vxmlUrl;

    public void setVxmlUrl(String vxmlUrl) {
    	this.vxmlUrl = vxmlUrl;
    }

	@Override
	public void handle(MotechEvent event) {

        String appointmentId = EventKeys.getAppointmentId(event);
        if (appointmentId == null) {
            log.error("Can not handle the Appointment Reminder Event: " + event +
                     ". The event is invalid - missing the " + EventKeys.APPOINTMENT_ID_KEY + " parameter");
            return;
        }

        Appointment appointment = patientDao.getAppointment(appointmentId);
        if (appointment == null) {
            log.error("Can not handle the Appointment Reminder Event: " + event +
                     ". The event is invalid - no appointment for id " + appointmentId);
            return;
        }

        Patient patient = patientDao.get(appointment.getPatientId());

        String url = vxmlUrl + "?aptId=" + appointmentId;

        VoiceMessageType mt = new VoiceMessageType();
        mt.setPriority(MessagePriority.MEDIUM);
        mt.setvXmlUrl(url);

        OutboundVoiceMessage msg = new OutboundVoiceMessage();
        msg.setPartyId(patient.getId());
        msg.setStatus(OutboundVoiceMessageStatus.PENDING);
        msg.setCreationTime(new Date());
        msg.setVoiceMessageType(mt);

        outboundVoiceMessageDao.add(msg);
    }

    @Override
	public String getIdentifier() {
		return UPCOMING_UNSCHEDULED;
	}

}
