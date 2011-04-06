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
package org.motechproject.server.appointmentreminder;

import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.motechproject.appointmentreminder.model.Appointment;
import org.motechproject.appointmentreminder.model.Patient;
import org.motechproject.context.Context;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.SchedulableJob;
import org.motechproject.server.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Responsible for listening for <code>{@link ScheduleAppointmentReminderEventType}</code>
 * events with destination
 * 
 * @author yyonkov
 * 
 */
@Component
public class ScheduleAppointmentReminderHandler implements EventListener {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	public final static String SCHEDULE_APPOINTMENT_REMINDER = "ScheduleAppointmentReminder";
	
	@Autowired
	private Context context;
	@Autowired
	private PatientDAO patientDAO;
	
	@Override
	public void handle(MotechEvent event) {
		try {
			String patientId = (String) event.getParameters().get(MotechEvent.SCHEDULE_PATIENT_ID_KEY_NAME);
			String appointmentId = (String) event.getParameters().get(MotechEvent.SCHEDULE_APPOINTMENT_ID_KEY_NAME);
			Patient patient = patientDAO.get(patientId);
			Appointment appointment = patientDAO.getAppointment(appointmentId);
			SchedulableJob schedulableJob = new SchedulableJob(event, String.format("0 0 %d * * ?", patient.getPreferences().getBestTimeToCall()),appointment.getReminderWindowStart(), appointment.getReminderWindowEnd());
			context.getMotechSchedulerGateway().scheduleJob(schedulableJob);
		} catch (RuntimeException e) {
		    for (StackTraceElement el : e.getStackTrace()) {
		        logger.error(el.getFileName() + ":" + el.getLineNumber() + ">> " + el.getMethodName() + "()");
		    }			
		    // TODO break the exceptions on transient and non-transient and throw the appropriate one
			throw e;
		}
	}

	@Override
	public String getIdentifier() {
		return SCHEDULE_APPOINTMENT_REMINDER;
	}
}
