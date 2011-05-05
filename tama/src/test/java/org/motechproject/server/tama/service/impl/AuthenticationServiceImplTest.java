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
package org.motechproject.server.tama.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.motechproject.server.tama.service.AuthenticationService;
import org.motechproject.tama.api.dao.PatientDAO;
import org.motechproject.tama.api.model.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthenticationServiceImplTest {
	
    @InjectMocks
    AuthenticationService service = new AuthenticationServiceImpl();

    @Mock
    private PatientDAO patientDao;
    
    private Patient patient;
    private String patientId = UUID.randomUUID().toString();
	
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        patient = new Patient();
    	patient.setId(patientId);
     }
	
    @Test
    public void getPatientIdByPhoneNumberTest() throws Exception {
    	String phoneNumber = "1234";
    	List<Patient> patients = new ArrayList<Patient>();
    	patients.add(patient);
    	Mockito.when(patientDao.findByPhoneNumber("SIP/" + phoneNumber)).thenReturn(patients);
    	
    	Assert.assertEquals(patientId, service.getPatientIdByPhoneNumber(phoneNumber));
    	Assert.assertNull(service.getPatientIdByPhoneNumber("4321"));
    }
    
    public void verifyPasscodeTest() throws Exception {
    	String passcode = "passwd";
    	patient.setPasscode(passcode);
    	Mockito.when(patientDao.get(patientId)).thenReturn(patient);
    	
    	Assert.assertTrue(service.verifyPasscode(patientId, passcode));

    	Assert.assertFalse(service.verifyPasscode(patientId, "wrong passwd"));
    	Assert.assertFalse(service.verifyPasscode("wrong id", passcode));
    	
    }
    
}

