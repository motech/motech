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
package org.motechproject.server.appointmentreminder.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.motechproject.appointmentreminder.dao.PatientDAO;
import org.motechproject.appointmentreminder.model.Patient;
import org.motechproject.model.InitiateCallData;
import org.motechproject.appointmentreminder.model.Appointment;
import org.motechproject.server.service.ivr.IVRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

/**
 * Appointment Reminder Service Unit tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/testApplicationContext.xml"})
public class AppointmentReminderServiceImplTest {

    @Autowired
    private AppointmentReminderServiceImpl appointmentReminderService;


    @Test
    public void testRemindPatientAppointment() throws Exception {

        IVRService ivrServiceMock = mock(IVRService.class);
        PatientDAO patientDaoMock = mock(PatientDAO.class);

        Appointment appointment = new Appointment();
        appointment.setPatientId("1p");

        Patient patient = new Patient();
        patient.setPhoneNumber("1001");

        when(patientDaoMock.getAppointment(Mockito.anyString())).thenReturn(appointment);
        when(patientDaoMock.get(appointment.getPatientId())).thenReturn(patient);

        appointmentReminderService.setIvrService(ivrServiceMock);
        appointmentReminderService.setPatientDao(patientDaoMock);

        appointmentReminderService.remindPatientAppointment("1a");

        verify(ivrServiceMock, times(1)).initiateCall(Mockito.any(InitiateCallData.class));
    }
}
