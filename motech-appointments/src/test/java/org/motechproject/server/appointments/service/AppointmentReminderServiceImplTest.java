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
package org.motechproject.server.appointments.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.motechproject.appointmentreminder.model.Appointment;
import org.motechproject.appointmentreminder.model.Patient;
import org.motechproject.appointmentreminder.model.Visit;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.event.EventRelay;
import org.springframework.test.context.ContextConfiguration;

import java.util.Calendar;
import java.util.Date;

import static org.mockito.Mockito.*;

/**
 * Appointment Reminder Service Unit tests
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class AppointmentReminderServiceImplTest {

    @InjectMocks
    AppointmentReminderServiceImpl appointmentReminderService = new AppointmentReminderServiceImpl();

    @Mock
    private PatientDAO patientDAOMock;

    @Mock
    private EventRelay eventRelayMock;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    // These offsets all start from today and are applied in the following order
    // startOffset, endOffset, visitOffset
    // You'll need to do the math on your own.
    private void setTestData(int startOffset, int endOffset,
                             boolean setVisits, int visitOffset)
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, startOffset);

        Appointment appointment = new Appointment();
        appointment.setReminderWindowStart(cal.getTime());

        cal.add(Calendar.DATE, endOffset);
        appointment.setReminderWindowEnd(cal.getTime());
        appointment.setPatientId("1p");

        Patient patient = new Patient();
        patient.setPhoneNumber("1001");

        when(patientDAOMock.getAppointment(Mockito.anyString())).thenReturn(appointment);
        when(patientDAOMock.get(appointment.getPatientId())).thenReturn(patient);

        if (setVisits) {
            Visit v = new Visit();
            cal.add(Calendar.DATE, visitOffset);
            v.setVisitDate(cal.getTime());

            patient.addVisit(v);
        }

    }

    private  void setTestData(int startOffset, int endOffset,
                             boolean setVisits, int visitOffset,
                             Date reminderDate)
    {
        setTestData(startOffset, endOffset, setVisits, visitOffset);

        Appointment appointment = patientDAOMock.getAppointment("");
    }

    /*
    The following tests verify behavior assuming we are inside an apt window, but the visit is
    in the following states

        no visit
        before window start
        on window start
        in window
        on window end
        after window end
     */
    @Test
    public void testRemindPatientAppointment_VisitBeforeWindow() throws Exception {
        setTestData(-3, 7, true, -30);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(1)).sendEventMessage(Mockito.any(MotechEvent.class));
    }

    @Test
    public void testRemindPatientAppointment_VisitOnWindowStart() throws Exception {
        setTestData(-3, 7, true, -4);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(0)).sendEventMessage(Mockito.any(MotechEvent.class));
    }

    @Test
    public void testRemindPatientAppointment_VisitInWindow() throws Exception {
        setTestData(-3, 7, true, -2);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(0)).sendEventMessage(Mockito.any(MotechEvent.class));
    }

    @Test
    public void testRemindPatientAppointment_VisitOnWindowEnd() throws Exception {
        setTestData(-3, 7, true, 0);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(0)).sendEventMessage(Mockito.any(MotechEvent.class));
    }


    @Test
    public void testRemindPatientAppointment_VisitAfterWindowEnd() throws Exception {
        setTestData(-3, 7, true, 7);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(1)).sendEventMessage(Mockito.any(MotechEvent.class));
    }

    /*
    The following tests verify behavior assuming an appointment window is in the following states

        before window start
        on window start
        in window
        on window end
        after window end
     */
    @Test
    public void testRemindPatientAppointment_beforeWindowStart() throws Exception {
        setTestData(30, 7, false, 0);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(0)).sendEventMessage(Mockito.any(MotechEvent.class));
    }

    @Test
    public void testRemindPatientAppointment_onWindowStart() throws Exception {
        setTestData(0, 7, false, 0);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(1)).sendEventMessage(Mockito.any(MotechEvent.class));
    }

    @Test
    public void testRemindPatientAppointment_inWindow() throws Exception {
        setTestData(-3, 7, false, 0);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(1)).sendEventMessage(Mockito.any(MotechEvent.class));
    }

    @Test
    public void testRemindPatientAppointment_onWindowEnd() throws Exception {
        setTestData(-7, 7, false, 0);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(1)).sendEventMessage(Mockito.any(MotechEvent.class));
    }


    @Test
    public void testRemindPatientAppointment_afterWindowEnd() throws Exception {
        setTestData(-30, 7, false, 0);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(eventRelayMock, times(0)).sendEventMessage(Mockito.any(MotechEvent.class));
    }
}
