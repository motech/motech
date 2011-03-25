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
package org.motechproject.server;

import org.motechproject.dao.PatientDao;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Patient;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.service.AppointmentReminderService;
import org.motechproject.server.service.AppointmentReminderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Handles Remind Appointment Events
 * 
 * @author Igor
 * 
 */
@Component
public class RemindAppointmentEventHandler implements EventListener {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

    public final static String APPOINTMENT_REMINDER = "AppointmentReminder";
    public final static String APPOINTMENT_ID_KEY = "AppointmentID";

    @Autowired
    AppointmentReminderService appointmentReminderService;

	@Override
	public void handle(MotechEvent event) {

        String appointmentId = null;
        try {
            appointmentId = (String) event.getParameters().get(APPOINTMENT_ID_KEY);
        } catch (ClassCastException e) {
            log.error("Can not handle the Appointment Reminder. Event: " + event + ". The event is invalid " +
                    APPOINTMENT_ID_KEY + " parameter is not a String" );
            return;
        }

        if (appointmentId == null) {
             log.error("Can not handle the Appointment Reminder. Event: " + event +
                     ". The event is invalid - missing the " +
                    APPOINTMENT_ID_KEY + " parameter" );
            return;
        }

        appointmentReminderService.remindPatientAppointment(appointmentId);
    }

	@Override
	public String getIdentifier() {
		return APPOINTMENT_REMINDER;
	}

}
