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
package org.motechproject.server.appointmentreminder.service;

import org.apache.commons.lang.time.DateUtils;
import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.motechproject.appointmentreminder.model.Appointment;
import org.motechproject.appointmentreminder.model.AppointmentReminder;
import org.motechproject.appointmentreminder.model.Patient;
import org.motechproject.appointmentreminder.model.Visit;
import org.motechproject.context.Context;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.appointmentreminder.eventtype.ReminderCallCompleteEventType;
import org.motechproject.server.appointmentreminder.eventtype.ReminderCallIncompleteEventType;
import org.motechproject.server.service.ivr.CallInitiationException;
import org.motechproject.server.service.ivr.CallRequest;
import org.motechproject.server.service.ivr.IVRService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 *
 */
public class AppointmentReminderServiceImpl implements AppointmentReminderService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private IVRService ivrService = Context.getInstance().getIvrService();

    @Autowired
    PatientDAO patientDao;

    //Interim implementation
    String  appointmentReminderVmlUrl = "http://10.0.1.29:8080/TamaIVR/reminder/doc";

	int timeOut;
    public final static String SCHEDULE_APPOINTMENT_REMINDER = "ScheduleAppointmentReminder";
    
    public void setAppointmentReminderVmlUrl(String appointmentReminderVmlUrl) {
    	this.appointmentReminderVmlUrl = appointmentReminderVmlUrl;
    }

    public void setTimeOut(int timeOut) {
    	this.timeOut = timeOut;
    }

    @Override
    public void remindPatientAppointment(String appointmentId) {

        //TODO - handle DAO exceptions
        Appointment appointment = patientDao.getAppointment(appointmentId);
        Patient patient = patientDao.get(appointment.getPatientId());

        long messageId = 1;
        String phone = patient.getPhoneNumber();

        Date today = DateUtils.truncate(new Date(), Calendar.DATE);

        // Patient is in window
        Date reminderWindowStart = DateUtils.truncate(appointment.getReminderWindowStart(), Calendar.DATE);
        Date reminderWindowEnd = DateUtils.truncate(appointment.getReminderWindowEnd(), Calendar.DATE);
        boolean inWindow = false;
        boolean visitedClinic = false;
        boolean alreadyReminded = false;

        if (reminderWindowStart.compareTo(today) <= 0 &&
                reminderWindowEnd.compareTo(today) >= 0) {
            inWindow = true;
        }

        Set<Visit> visits = patient.getVisits();
        for (Visit v : visits) {
            Date visitDate = DateUtils.truncate(v.getVisitDate(), Calendar.DATE);
            if (reminderWindowStart.compareTo(visitDate) <= 0 &&
                    reminderWindowEnd.compareTo(visitDate) >= 0) {
                visitedClinic = true;
            }
        }

        if (!inWindow) {
            log.info("Ignoring reminder event for patientId=" + patient.getClinicPatientId() +
                             "appointmentId=" + appointmentId +
                             " outside of window start=" + reminderWindowStart +
                             " today=" + today + " end=" + reminderWindowEnd);
        }

        if (visitedClinic) {
            log.info("Ignoring reminder event for patientId=" + patient.getClinicPatientId() +
                             "appointmentId=" + appointmentId +
                             " already visited clinic");
        }

        if (inWindow && !visitedClinic) {
            // Get list of Appointment Reminders
            Set<AppointmentReminder> reminders = appointment.getReminders();

            // See if there is a completed or open reminder for today
            for (AppointmentReminder r : reminders) {
                Date reminderDate = DateUtils.truncate(r.getReminderDate(), Calendar.DATE);

                // See if this reminder has already been sent
                if (reminderDate.compareTo(today) == 0 &&
                        r.getStatus() != AppointmentReminder.Status.INCOMPLETE) {
                    log.info("Ignoring duplicate reminder event for patientId=" + patient.getClinicPatientId() +
                                     "appointmentId=" + appointmentId);
                    alreadyReminded = true;
                }
            }

            if (!alreadyReminded) {
                AppointmentReminder ar = new AppointmentReminder(today,
                                                                AppointmentReminder.Status.REQUESTED);
                appointment.addReminder(ar);

                // Ignore any optimistic locks.  The event should be rehandled and next time through
                // my write will either succeed, or this reminder will have been handled by someone else
                // I'm nt happy with this solution since we can't wrap the IVR call with this update
                // it is still possible that calls will be recorded as sent that are not.
                patientDao.updateAppointment(appointment);

                try {
                    CallRequest callRequest = new CallRequest(messageId, phone,
                                                              timeOut, appointmentReminderVmlUrl);

                    Map<String, Object> messageParameters = new HashMap<String, Object>();
                    messageParameters.put("AppointmentID", appointmentId);
                    messageParameters.put("CallDate", today);
                    MotechEvent incompleteEvent = new MotechEvent("Incomplete Reminder Call",
                                                                  ReminderCallIncompleteEventType.getInstance().getKey(),
                                                                  messageParameters);

                    callRequest.setOnBusyEvent(incompleteEvent);
                    callRequest.setOnFailureEvent(incompleteEvent);
                    callRequest.setOnNoAnswerEvent(incompleteEvent);

                    MotechEvent successEvent = new MotechEvent("Completed Reminder Call",
                                                               ReminderCallCompleteEventType.getInstance().getKey(),
                                                               messageParameters);

                    callRequest.setOnSuccessEvent(successEvent);

                    ivrService.initiateCall(callRequest);
                } catch (CallInitiationException e) {
                    log.warn("Unable to initiate call to patientId=" + patient.getClinicPatientId() +
                                     " for appointmentId=" + appointmentId + e.getMessage());
                    ar.setStatus(AppointmentReminder.Status.INCOMPLETE);
                    patientDao.updateAppointment(appointment);
                }
            }
        }
    }

    @Override
    public void reminderCallCompleted(String appointmentId, Date callDate)
    {
        //TODO - handle DAO exceptions
        Appointment appointment = patientDao.getAppointment(appointmentId);

        Set<AppointmentReminder> reminders = appointment.getReminders();

        for (AppointmentReminder r : reminders) {
            Date reminderDate = DateUtils.truncate(r.getReminderDate(), Calendar.DATE);

            if (reminderDate.compareTo(callDate) == 0) {
                r.setStatus(AppointmentReminder.Status.COMPLETED);
                patientDao.updateAppointment(appointment);
            }
        }
    }

    @Override
    public void reminderCallIncompleted(String appointmentId, Date callDate)
    {
        //TODO - handle DAO exceptions
        Appointment appointment = patientDao.getAppointment(appointmentId);

        Set<AppointmentReminder> reminders = appointment.getReminders();

        for (AppointmentReminder r : reminders) {
            Date reminderDate = DateUtils.truncate(r.getReminderDate(), Calendar.DATE);

            if (reminderDate.compareTo(callDate) == 0) {
                // Only move calls in REQUESTED to INCOMPLETE.  If it was somehow set to COMPLETE
                // leave it there.
                if (r.getStatus() == AppointmentReminder.Status.REQUESTED) {
                    r.setStatus(AppointmentReminder.Status.INCOMPLETE);
                    patientDao.updateAppointment(appointment);
                }
            }
        }
    }
}
