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
package org.motechproject.openmrs.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.openmrs.model.Appointment;
import org.motechproject.openmrs.service.AppointmentService;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.test.BaseModuleContextSensitiveTest;

public class AppointmentServiceImplIT extends BaseModuleContextSensitiveTest {
	
    @Before
    public void setUp() throws Exception {
        initializeInMemoryDatabase();
        executeDataSet("simple-patient-dataset.xml");
    }

    @After
    public void tearDown() throws Exception {
    }
    
    @Test
    public void testAppointmentService() throws Exception {
        
        PatientService patientService = Context.getPatientService();
        Patient patient = patientService.getPatient(2);
        
        Appointment appoinment1 = new Appointment();
        appoinment1.setPatient(patient);
        appoinment1.setWindowStartDate(DateUtils.addDays(new Date(), 7));
        appoinment1.setWindowEndDate(DateUtils.addDays(new Date(), 14));
        
        Appointment appoinment2 = new Appointment();
        appoinment2.setPatient(patient);
        appoinment2.setWindowStartDate(DateUtils.addDays(new Date(), 20));
        appoinment2.setWindowEndDate(DateUtils.addDays(new Date(), 30));
        
        AppointmentService appointmentService = Context.getService(AppointmentService.class);
        appointmentService.saveAppointment(appoinment1);
        appointmentService.saveAppointment(appoinment2);
        
        assertNotNull(appoinment1.getId());
        assertNotNull(appoinment2.getId());
        assertEquals(2, appointmentService.getAppointments(patient).size());
        
        //TODO: add some assertions for couchdb and jms messaging
        
    }
}
