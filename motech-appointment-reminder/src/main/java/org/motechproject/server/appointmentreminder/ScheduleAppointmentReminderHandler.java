/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.motechproject.server.appointmentreminder;

import org.motechproject.dao.PatientDao;
import org.motechproject.model.Appointment;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Patient;
import org.motechproject.model.SchedulableJob;
import org.motechproject.scheduler.MotechSchedulerGateway;
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
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	public final static String SCHEDULE_APPOINTMENT_REMINDER = "ScheduleAppointmentReminder";

	@Autowired
	private MotechSchedulerGateway motechSchedulerGateway;
	@Autowired
	private PatientDao patientDao;

	@Override
	public void handle(MotechEvent event) {
		String patientId = (String) event.getParameters().get(MotechEvent.SCHEDULE_PATIENT_ID_KEY_NAME);
		String appointmentId = (String) event.getParameters().get(MotechEvent.SCHEDULE_APPOINTMENT_ID_KEY_NAME);
		Patient patient = patientDao.get(patientId);
		Appointment appointment = patientDao.getAppointment(appointmentId);
		event.getParameters().put(MotechEvent.START_TIME_KEY_NAME, appointment.getWindowStartDate());
		event.getParameters().put(MotechEvent.END_TIME_KEY_NAME, appointment.getWindowEndDate());		
		// TODO build cronExpression from Appointment information. 
        SchedulableJob schedulableJob = new SchedulableJob(event, String.format("0 0 %d * * ?", patient.getBestCallTime()));
        motechSchedulerGateway.scheduleJob(schedulableJob);
	}

	@Override
	public String getIdentifier() {
		return SCHEDULE_APPOINTMENT_REMINDER;
	}

}
